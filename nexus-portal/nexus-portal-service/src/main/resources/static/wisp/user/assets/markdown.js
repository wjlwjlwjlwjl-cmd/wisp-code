/* Minimal, dependency-free Markdown renderer for WispCode.
 * Exposes window.WispMD.render(src) -> safe HTML string.
 * Supports: fenced code, inline code, headings, bold/italic/strike, links,
 * images, blockquote, hr, ordered/unordered lists (nested by indent), tables,
 * task lists, paragraphs. HTML is escaped to avoid XSS. */
(function (global) {
  "use strict";

  function escapeHtml(s) {
    return String(s)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  function safeUrl(u) {
    var t = String(u || "").trim();
    return /^(https?:|mailto:|tel:|#|\/|\.)/i.test(t) ? t : "#";
  }

  // inline spans: operates on already-escaped text
  function inline(text) {
    var out = text;
    // images ![alt](url)
    out = out.replace(/!\[([^\]]*)\]\(([^)\s]+)(?:\s+"[^"]*")?\)/g, function (_, alt, url) {
      return '<img src="' + safeUrl(url) + '" alt="' + alt + '" style="max-width:100%">';
    });
    // links [text](url)
    out = out.replace(/\[([^\]]+)\]\(([^)\s]+)(?:\s+"[^"]*")?\)/g, function (_, tx, url) {
      return '<a href="' + safeUrl(url) + '" target="_blank" rel="noopener noreferrer">' + tx + "</a>";
    });
    // autolink bare urls
    out = out.replace(/(^|[\s(])((?:https?:\/\/)[^\s<)]+)/g, function (_, pre, url) {
      return pre + '<a href="' + safeUrl(url) + '" target="_blank" rel="noopener noreferrer">' + url + "</a>";
    });
    // bold + italic
    out = out.replace(/\*\*\*([^*]+)\*\*\*/g, "<strong><em>$1</em></strong>");
    out = out.replace(/___([^_]+)___/g, "<strong><em>$1</em></strong>");
    out = out.replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>");
    out = out.replace(/__([^_]+)__/g, "<strong>$1</strong>");
    out = out.replace(/(^|[^*])\*([^*\n]+)\*/g, "$1<em>$2</em>");
    out = out.replace(/(^|[^_\w])_([^_\n]+)_/g, "$1<em>$2</em>");
    // strikethrough
    out = out.replace(/~~([^~]+)~~/g, "<del>$1</del>");
    // inline code (do last so its content isn't re-emphasized)
    out = out.replace(/`([^`\n]+)`/g, function (_, code) { return "<code>" + code + "</code>"; });
    return out;
  }

  function render(src) {
    if (src === null || src === undefined) return "";
    src = String(src).replace(/\r\n?/g, "\n");
    var lines = src.split("\n");
    var html = [];
    var i = 0;
    var para = [];
    function flushPara() {
      if (para.length) {
        html.push("<p>" + inline(escapeHtml(para.join("\n"))).replace(/\n/g, "<br>") + "</p>");
        para = [];
      }
    }
    while (i < lines.length) {
      var line = lines[i];

      // fenced code block
      var fence = line.match(/^```(\w*)/);
      if (fence) {
        flushPara();
        var lang = fence[1] || "";
        var buf = [];
        i++;
        while (i < lines.length && !/^```/.test(lines[i])) { buf.push(lines[i]); i++; }
        i++; // skip closing fence
        html.push('<pre data-lang="' + escapeHtml(lang) + '"><code>' + escapeHtml(buf.join("\n")) + "</code></pre>");
        continue;
      }

      // horizontal rule
      if (/^ {0,3}([-*_])\s*(?:\1\s*){2,}$/.test(line)) {
        flushPara(); html.push("<hr>"); i++; continue;
      }

      // heading
      var h = line.match(/^ {0,3}(#{1,6})\s+(.*)$/);
      if (h) {
        flushPara();
        var lv = h[1].length;
        html.push("<h" + lv + ">" + inline(escapeHtml(h[2].replace(/\s+#+\s*$/, ""))) + "</h" + lv + ">");
        i++; continue;
      }

      // blockquote (consume consecutive)
      if (/^ {0,3}>\s?/.test(line)) {
        flushPara();
        var q = [];
        while (i < lines.length && /^ {0,3}>\s?/.test(lines[i])) { q.push(lines[i].replace(/^ {0,3}>\s?/, "")); i++; }
        html.push("<blockquote>" + render(q.join("\n")) + "</blockquote>");
        continue;
      }

      // table: header row + separator row
      if (/\|/.test(line) && i + 1 < lines.length && /^\s*\|?[\s:|-]+\|?\s*$/.test(lines[i + 1]) && /-/.test(lines[i + 1])) {
        flushPara();
        var header = splitRow(line);
        var align = splitRow(lines[i + 1]).map(function (c) {
          var l = /^:/.test(c), r = /:$/.test(c);
          return l && r ? "center" : r ? "right" : l ? "left" : "";
        });
        i += 2;
        var rows = [];
        while (i < lines.length && /\|/.test(lines[i]) && lines[i].trim() !== "") { rows.push(splitRow(lines[i])); i++; }
        var t = '<table><thead><tr>';
        header.forEach(function (c, idx) { t += '<th' + (align[idx] ? ' style="text-align:' + align[idx] + '"' : "") + ">" + inline(escapeHtml(c)) + "</th>"; });
        t += "</tr></thead><tbody>";
        rows.forEach(function (r) {
          t += "<tr>";
          header.forEach(function (_, idx) { t += "<td" + (align[idx] ? ' style="text-align:' + align[idx] + '"' : "") + ">" + inline(escapeHtml(r[idx] || "")) + "</td>"; });
          t += "</tr>";
        });
        t += "</tbody></table>";
        html.push(t);
        continue;
      }

      // lists (unordered / ordered / task), nested by indentation
      var li = line.match(/^(\s*)([-*+]|\d+\.)\s+(.*)$/);
      if (li) {
        flushPara();
        var stack = [];
        while (i < lines.length) {
          var m = lines[i].match(/^(\s*)([-*+]|\d+\.)\s+(.*)$/);
          if (!m) break;
          var indent = m[1].length;
          var ordered = /\d/.test(m[2]);
          var content = m[3];
          var task = content.match(/^\[([ xX])\]\s+(.*)$/);
          var itemHtml;
          if (task) {
            var checked = task[1].toLowerCase() === "x";
            itemHtml = '<li class="task"><label><input type="checkbox" disabled' + (checked ? " checked" : "") + "> " + inline(escapeHtml(task[2])) + "</label></li>";
          } else {
            itemHtml = "<li>" + inline(escapeHtml(content)) + "</li>";
          }
          // manage nesting stack of {type, indent, open}
          while (stack.length && indent < stack[stack.length - 1].indent) { html.push("</" + stack.pop().tag + ">"); }
          if (!stack.length || indent > stack[stack.length - 1].indent || (stack[stack.length - 1].indent === indent && stack[stack.length - 1].tag !== (ordered ? "ol" : "ul"))) {
            var tag = ordered ? "ol" : "ul";
            html.push("<" + tag + ">");
            stack.push({ tag: tag, indent: indent });
          }
          html.push(itemHtml);
          i++;
        }
        while (stack.length) { html.push("</" + stack.pop().tag + ">"); }
        continue;
      }

      // blank line ends a paragraph
      if (line.trim() === "") { flushPara(); i++; continue; }

      // otherwise accumulate paragraph text
      para.push(line);
      i++;
    }
    flushPara();
    return html.join("\n");
  }

  function splitRow(row) {
    var s = row.trim().replace(/^\|/, "").replace(/\|$/, "");
    return s.split("|").map(function (c) { return c.trim(); });
  }

  global.WispMD = { render: render, escape: escapeHtml };
})(window);
