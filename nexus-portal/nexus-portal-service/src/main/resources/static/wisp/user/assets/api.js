/* WispCode frontend shared API + helpers (frontend only, no backend changes) */
(function (global) {
  "use strict";

  // ---- config ------------------------------------------------------------
  // Backend gateway entry. All requests go through the gateway which routes
  // /wisp/** to the wispcode(portal) service. Pages are hosted under the
  // whitelisted path /wisp/user/** so that loading them and calling the APIs
  // are same-origin (no CORS) when opened via the gateway.
  // Override on a page with: <script>window.WISP_API_BASE="http://host:port"</script>
  var API = {
    base: (global.WISP_API_BASE != null ? global.WISP_API_BASE : "") + "/wisp"
  };
  var SUCCESS_CODE = 200000; // ResultCode.SUCCESS

  // ---- JWT / auth failure ResultCodes (from nexus-gateway AuthFilter) ----
  // 网关对每种令牌失效都返回 HTTP 401 + R 体，code 为下列 401xxx：
  var AUTH_CODES = {
    401000: "TOKEN_EMPTY",            // 令牌不能为空
    401001: "TOKEN_INVALID",          // 令牌已过期或验证不正确
    401002: "TOKEN_OVERTIME",         // 令牌已过期
    401003: "LOGIN_STATUS_OVERTIME",  // 登录状态已过期（Redis TTL 到期）
    401004: "TOKEN_CHECK_FAILED"      // 令牌验证失败（解析出的用户信息不完整）
  };
  var AUTH_REASONS = {
    401000: "未检测到登录令牌，请重新登录",
    401001: "登录令牌已过期或校验不通过，请重新登录",
    401002: "登录令牌已过期，请重新登录",
    401003: "登录状态已过期（超时），请重新登录",
    401004: "登录令牌校验失败，请重新登录"
  };
  var REASON_KEY = "wisp_logout_reason";
  function isAuthCode(code) { return Object.prototype.hasOwnProperty.call(AUTH_CODES, code); }

  // ---- token / session ---------------------------------------------------
  var TOKEN_KEY = "wisp_token";
  var USER_KEY = "wisp_user";
  var firingExpire = false;
  var auth = {
    token: function () { return localStorage.getItem(TOKEN_KEY) || ""; },
    setToken: function (t, expires) {
      localStorage.setItem(TOKEN_KEY, t || "");
      if (expires) localStorage.setItem("wisp_exp", String(Date.now() + expires));
    },
    user: function () {
      try { return JSON.parse(localStorage.getItem(USER_KEY) || "null"); } catch (e) { return null; }
    },
    setUser: function (u) { localStorage.setItem(USER_KEY, JSON.stringify(u || null)); },
    clear: function () { localStorage.removeItem(TOKEN_KEY); localStorage.removeItem(USER_KEY); localStorage.removeItem("wisp_exp"); },
    isLogged: function () { return !!auth.token(); },
    reason: function () { var r = sessionStorage.getItem(REASON_KEY) || ""; sessionStorage.removeItem(REASON_KEY); return r; },
    setReason: function (m) { try { sessionStorage.setItem(REASON_KEY, m || ""); } catch (e) {} },
    require: function () {
      if (!auth.isLogged()) { location.href = "login.html?redirect=" + encodeURIComponent(location.pathname.split("/").pop() + location.search); return false; }
      return true;
    },
    // 用户主动退出
    logout: function () { auth.clear(); auth.setReason("您已安全退出"); location.href = "login.html"; },
    // 令牌失效：清空并统一跳转到登录页重新获取（每种失效都会回到这里）
    expired: function (msg) {
      auth.clear();
      auth.setReason(msg || "登录状态已失效，请重新登录");
      if (firingExpire) return;
      firingExpire = true;
      // 避免在登录页自身重复跳转造成抖动
      if (/login\.html/.test(location.pathname)) { firingExpire = false; return; }
      location.href = "login.html";
    }
  };

  // ---- low level fetch ---------------------------------------------------
  function buildQuery(obj) {
    var parts = [];
    Object.keys(obj).forEach(function (k) {
      if (obj[k] === undefined || obj[k] === null) return;
      parts.push(encodeURIComponent(k) + "=" + encodeURIComponent(obj[k]));
    });
    return parts.join("&");
  }

  function headers(withAuth) {
    var h = {};
    if (withAuth) { var t = auth.token(); if (t) h.Authorization = t; }
    return h;
  }

  function handleR(json) {
    // unwrap R<T>. Throws if failed.
    if (json && typeof json.code !== "undefined" && json.code !== null) {
      if (json.code === SUCCESS_CODE) return json.data;
      if (isAuthCode(json.code)) {
        var ae = new Error(AUTH_REASONS[json.code] || json.msg || "登录状态已失效，请重新登录");
        ae.needAuth = true; ae.code = json.code; auth.expired(ae.message); throw ae;
      }
      var err = new Error(json.msg || ("请求失败 (code=" + json.code + ")"));
      err.code = json.code; err.payload = json; throw err;
    }
    return json; // raw (legacy) response without R envelope
  }

  async function req(url, opts) {
    opts = opts || {};
    var res;
    try {
      res = await fetch(url, opts);
    } catch (e) {
      throw new Error("网络错误：无法连接服务");
    }
    var text = await res.text();
    var json = null;
    try { json = text ? JSON.parse(text) : null; } catch (e) { json = text; }
    var code = json && typeof json === "object" ? json.code : undefined;
    // 令牌失效：HTTP 401/403（网关）或 401xxx 业务码，一律回登录页重新获取
    if (res.status === 401 || res.status === 403 || isAuthCode(code)) {
      var reason = AUTH_REASONS[code] || (json && json.msg) || "登录状态已失效，请重新登录";
      auth.expired(reason);
      var e401 = new Error(reason); e401.needAuth = true; e401.code = code || res.status; throw e401;
    }
    if (!res.ok && (json === null || typeof json.code === "undefined")) {
      throw new Error("HTTP " + res.status + (typeof json === "string" ? " " + json.slice(0, 120) : ""));
    }
    return json;
  }

  // GET with query params
  function get(path, params, withAuth) {
    var url = API.base + path;
    var qs = params ? buildQuery(params) : "";
    if (qs) url += (url.indexOf("?") >= 0 ? "&" : "?") + qs;
    return req(url, { method: "GET", headers: headers(!!withAuth) });
  }
  // POST json (@RequestBody endpoints)
  function postJson(path, body, withAuth) {
    var url = API.base + path;
    return req(url, {
      method: "POST",
      headers: (function () { var h = { "Content-Type": "application/json" }; var a = headers(!!withAuth); Object.keys(a).forEach(function (k) { h[k] = a[k]; }); return h; })(),
      body: JSON.stringify(body || {})
    });
  }

  // ---- API surface -------------------------------------------------------
  // heuristic to fix agent endpoint (previewUrl/appType args swapped)
  function fixRetVO(d) {
    if (!d) return d;
    var looksUrl = function (s) { return typeof s === "string" && /^https?:\/\//i.test(s); };
    if (!looksUrl(d.previewUrl) && looksUrl(d.appType)) {
      var tmp = d.previewUrl; d.previewUrl = d.appType; d.appType = tmp;
    }
    return d;
  }

  var api = {
    // GATEWAY POLICY: AuthFilter only whitelists /wisp/user/**. Every other
    // /wisp/** call (app/detail, app/history, app/list/*, app/deploy*, generate,
    // edit ...) MUST carry the Authorization header or the gateway returns 401.
    // Only login/register/send_code omit it; get_info is whitelisted but its
    // controller reads the header itself, so we still send it.
    // user
    sendCode: async function (email) { return handleR(await get("/user/send_code", { email: email }, false)); },
    register: async function (p) { return handleR(await postJson("/user/register", p, false)); },
    login: async function (email, password) {
      var vo = handleR(await postJson("/user/login", { email: email, password: password }, false));
      if (!vo || !vo.accessToken) throw new Error("邮箱或密码错误");
      auth.setToken(vo.accessToken, vo.expires);
      return vo;
    },
    getInfo: async function () {
      var u = handleR(await get("/user/get_info", null, true));
      if (u) auth.setUser(u);
      return u;
    },
    // requirement + generation (@RequestBody JSON)
    genRequirement: async function (input) { return handleR(await postJson("/requirement/generate", { input: input }, true)); },
    genApp: async function (appId, appDoc) { return fixRetVO(handleR(await postJson("/app/generate", { appId: appId, appDoc: appDoc }, true))); },
    genAppAgent: async function (appId, appDoc) { return fixRetVO(handleR(await postJson("/agent/app/generate", { appId: String(appId), appDoc: appDoc }, true))); },
    editApp: async function (appId, newPrompt) { return fixRetVO(handleR(await postJson("/app/edit", { appId: appId, newPrompt: newPrompt }, true))); },
    // listing
    listMine: async function (current, size) { return handleR(await get("/app/list/mine", { current: current, size: size }, true)); },
    listDeploy: async function (current, size) { return handleR(await get("/app/list/deploy", { current: current, size: size }, true)); },
    detail: async function (appId) { return handleR(await get("/app/detail", { appId: appId }, true)); },
    history: async function (appId) { return handleR(await get("/app/history", { appId: appId }, true)); },
    deploy: async function (appId) { return handleR(await get("/app/deploy", { appId: appId }, true)); },
    deployCancel: async function (appId) { return handleR(await get("/app/deploy/cancel", { appId: appId }, true)); },
    // code-server ⻚⾯端代码预览：成功返回 URL 字符串，⽆权限时后端返回空
    vsUrl: async function (appId) { return handleR(await get("/app/vs", { appId: appId }, true)); },
    // 确认 vscode 在线编辑的改动（回传/落库），返回 Boolean
    vsConfirm: async function (appId) { return handleR(await get("/app/vs/confirm", { appId: appId }, true)); }
  };

  // ---- UI helpers --------------------------------------------------------
  function toast(msg, type) {
    var box = document.getElementById("toast-box");
    if (!box) { box = document.createElement("div"); box.id = "toast-box"; box.className = "toast"; document.body.appendChild(box); }
    var el = document.createElement("div");
    el.className = "t " + (type || "");
    el.textContent = msg;
    box.appendChild(el);
    setTimeout(function () { el.style.transition = ".3s"; el.style.opacity = "0"; el.style.transform = "translateX(20px)"; setTimeout(function () { el.remove(); }, 300); }, 3200);
  }

  function nav(active) {
    var logged = auth.isLogged();
    var u = auth.user();
    var links = [
      ["index.html", "概览", "home"],
      ["create.html", "生成应用", "create"],
      ["apps.html", "我的应用", "apps"],
      ["deployed.html", "已部署应用", "deployed"],
      ["profile.html", "个人中心", "profile"]
    ];
    var html = '<div class="nav">' +
      '<a class="brand" href="index.html"><span class="logo">⚡</span> WispCode</a>' +
      '<div class="links">' + links.map(function (l) {
        return '<a class="' + (l[2] === active ? "active" : "") + '" href="' + l[0] + '">' + l[1] + "</a>";
      }).join("") + "</div>" +
      '<div class="spacer"></div>';
    if (logged) {
      var name = (u && (u.username || u.email)) || "已登录";
      html += '<div class="user-chip"><span class="avatar">' + name.slice(0, 1).toUpperCase() + '</span>' +
        '<span>' + name + '</span><button class="btn ghost sm" onclick="WispAPI.auth.logout()">退出</button></div>';
    } else {
      html += '<a class="btn sm" href="login.html">登录</a>';
    }
    html += "</div>";
    var mount = document.getElementById("nav-mount");
    if (mount) mount.innerHTML = html; else document.body.insertAdjacentHTML("afterbegin", html);
  }

  function escape(s) {
    if (s === null || s === undefined) return "";
    s = String(s); var out = "", i, c;
    for (i = 0; i < s.length; i++) {
      c = s.charAt(i);
      out += (c === "&" ? "&amp;" : c === "<" ? "&lt;" : c === ">" ? "&gt;" : c === '"' ? "&quot;" : c === "'" ? "&#39;" : c);
    }
    return out;
  }
  // Map backend appType (either enum name from generate APIs, or numeric code
  // stored in app.app_type via AppType.getTypeNum: HTML=0, VUE3=1, VUE3_SPRING=2)
  // to a friendly display label.
  function appTypeLabel(t) {
    if (t === null || t === undefined || t === "") return "—";
    var s = String(t).trim().toUpperCase();
    var byName = { "HTML": "HTML", "VUE3": "VUE", "VUE": "VUE", "VUE3_SPRING": "VUE_SPRING", "VUE_SPRING": "VUE_SPRING" };
    if (byName[s]) return byName[s];
    var byCode = { "0": "HTML", "1": "VUE", "2": "VUE_SPRING" };
    if (byCode[s]) return byCode[s];
    return String(t);
  }
  function appTypeBadge(t) { return t ? '<span class="badge">' + escape(appTypeLabel(t)) + "</span>" : ""; }
  function fmtUrl(u) { return u && /^https?:\/\//i.test(u) ? u : (u ? "http://" + u : ""); }

  global.WispAPI = { api: api, auth: auth, toast: toast, nav: nav, escape: escape, appTypeBadge: appTypeBadge, appTypeLabel: appTypeLabel, fmtUrl: fmtUrl, SUCCESS_CODE: SUCCESS_CODE };
})(window);
