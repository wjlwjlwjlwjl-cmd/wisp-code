---
name: WispCode User Portal
description: A LeetCode/Nowcoder-grammar generation console — flat charcoal-or-paper surfaces, hairline structure, one gold action color, verdict-colored state pills.
colors:
  # dark world (default; api.js applies data-theme="dark")
  bg: "#1a1a1a"
  bg-soft: "#222222"
  panel: "#282828"
  panel-2: "#2f2f2f"
  inset: "#1f1f1f"
  line: "#3e3e3e"
  line-strong: "#545454"
  txt: "#eff1f5"
  muted: "#9ba1a9"
  brand: "#ffa116"
  brand-hover: "#ffab30"
  brand-ink: "#231800"
  brand-soft: "rgba(255,161,22,.12)"
  brand-line: "rgba(255,161,22,.4)"
  selection: "rgba(255,161,22,.3)"
  link: "#57a3f3"
  link-hover: "#7fbcff"
  info-soft: "rgba(87,163,243,.12)"
  info-txt: "#7fbcff"
  info-line: "rgba(87,163,243,.38)"
  ok: "#00b8a3"
  ok-ink: "#04322c"
  ok-soft: "rgba(0,184,163,.14)"
  ok-txt: "#3fd0bd"
  ok-line: "rgba(0,184,163,.4)"
  err: "#d93a40"
  err-ink: "#ffffff"
  err-soft: "rgba(229,72,77,.14)"
  err-txt: "#ff8a86"
  err-line: "rgba(229,72,77,.4)"
  code-bg: "#323232"
  code-inline-fg: "#ffb454"
  # light world (html[data-theme="light"])
  bg-light: "#f5f6f7"
  bg-soft-light: "#eceef1"
  panel-light: "#ffffff"
  panel-2-light: "#f7f8fa"
  inset-light: "#f7f8fa"
  line-light: "#d7dce4"
  line-strong-light: "#bfc6d1"
  txt-light: "#1f2329"
  muted-light: "#57606c"
  brand-hover-light: "#f59500"
  brand-ink-light: "#241700"
  brand-line-light: "rgba(255,161,22,.45)"
  link-light: "#0070d6"
  link-hover-light: "#005cad"
  info-soft-light: "rgba(0,112,214,.08)"
  info-txt-light: "#0062b8"
  info-line-light: "rgba(0,112,214,.35)"
  ok-light: "#00a68f"
  ok-ink-light: "#05342e"
  ok-soft-light: "rgba(0,166,143,.1)"
  ok-txt-light: "#067a68"
  ok-line-light: "rgba(0,166,143,.42)"
  err-soft-light: "rgba(217,58,64,.08)"
  err-txt-light: "#c2292f"
  err-line-light: "rgba(217,58,64,.38)"
  code-bg-light: "#f0f1f3"
  code-inline-fg-light: "#b8420f"
  # fixed fixtures (same in both themes, deliberately non-token)
  toast-panel: "#22262c"
  toast-txt: "#f2f4f7"
  toast-line: "rgba(255,255,255,.08)"
  toast-ok-dot: "#19c9a9"
  toast-err-dot: "#f4645f"
  spinner-track: "rgba(128,128,128,.4)"
  zebra: "rgba(128,128,128,.05)"
  press-inset: "rgba(0,0,0,.2)"
  preview-canvas: "#ffffff"
typography:
  display:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif"
    fontSize: "24px"
    fontWeight: 700
    lineHeight: 1.6
    letterSpacing: "-0.01em"
  wordmark:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif"
    fontSize: "22px"
    fontWeight: 700
    lineHeight: 1.6
  brand:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif"
    fontSize: "18px"
    fontWeight: 700
    letterSpacing: "0.2px"
  title:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif"
    fontSize: "16px"
    fontWeight: 700
    letterSpacing: "-0.01em"
  item-title:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif"
    fontSize: "15px"
    fontWeight: 700
    lineHeight: 1.4
  body:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif"
    fontSize: "14px"
    fontWeight: 400
    lineHeight: 1.6
  label:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif"
    fontSize: "13px"
    fontWeight: 600
  badge:
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif"
    fontSize: "12px"
    fontWeight: 600
    lineHeight: 1.5
  mono:
    fontFamily: "ui-monospace, SFMono-Regular, Menlo, Consolas, 'Liberation Mono', monospace"
    fontSize: "13px"
    lineHeight: 1.7
rounded:
  xs: "4px"
  sm: "6px"
  md: "8px"
  lg: "10px"
  xl: "12px"
  pill: "999px"
  circle: "50%"
spacing:
  xs: "4px"
  sm: "8px"
  md: "12px"
  base: "16px"
  lg: "24px"
  xl: "32px"
  2xl: "48px"
  3xl: "64px"
  nav: "56px"
components:
  button-primary:
    backgroundColor: "{colors.brand}"
    textColor: "{colors.brand-ink}"
    rounded: "{rounded.md}"
    padding: "9px 16px"
  button-primary-hover:
    backgroundColor: "{colors.brand-hover}"
    textColor: "{colors.brand-ink}"
  button-ghost:
    backgroundColor: "{colors.panel}"
    textColor: "{colors.txt}"
    rounded: "{rounded.md}"
    padding: "9px 16px"
  button-ghost-hover:
    backgroundColor: "{colors.bg-soft}"
    textColor: "{colors.txt}"
  button-ok:
    backgroundColor: "{colors.ok}"
    textColor: "{colors.ok-ink}"
    rounded: "{rounded.md}"
    padding: "9px 16px"
  button-danger:
    backgroundColor: "{colors.err}"
    textColor: "{colors.err-ink}"
    rounded: "{rounded.md}"
    padding: "9px 16px"
  button-sm:
    rounded: "{rounded.sm}"
    padding: "6px 12px"
  button-block:
    padding: "12px 16px"
    width: "100%"
  card:
    backgroundColor: "{colors.panel}"
    rounded: "{rounded.lg}"
    padding: "24px"
  card-toolbar:
    backgroundColor: "{colors.panel}"
    rounded: "{rounded.lg}"
    padding: "12px 16px"
  app-card:
    backgroundColor: "{colors.panel}"
    rounded: "{rounded.lg}"
    padding: "16px"
  auth-card:
    backgroundColor: "{colors.panel}"
    rounded: "{rounded.xl}"
    padding: "32px"
  input:
    backgroundColor: "{colors.inset}"
    textColor: "{colors.txt}"
    rounded: "{rounded.md}"
    padding: "10px 12px"
  badge:
    backgroundColor: "{colors.bg-soft}"
    textColor: "{colors.muted}"
    rounded: "{rounded.pill}"
    padding: "2px 10px"
  badge-on:
    backgroundColor: "{colors.ok-soft}"
    textColor: "{colors.ok-txt}"
    rounded: "{rounded.pill}"
    padding: "2px 10px"
  badge-off:
    backgroundColor: "{colors.bg-soft}"
    textColor: "{colors.muted}"
    rounded: "{rounded.pill}"
    padding: "2px 10px"
  step-active:
    backgroundColor: "{colors.brand-soft}"
    textColor: "{colors.txt}"
    rounded: "{rounded.md}"
    padding: "12px"
  nav:
    backgroundColor: "{colors.panel}"
    height: "{spacing.nav}"
    padding: "0 24px"
  toast:
    backgroundColor: "{colors.toast-panel}"
    textColor: "{colors.toast-txt}"
    rounded: "{rounded.md}"
    padding: "12px 16px"
  bubble-user:
    backgroundColor: "{colors.brand-soft}"
    textColor: "{colors.txt}"
    rounded: "{rounded.xl}"
    padding: "10px 14px"
  bubble-ai:
    backgroundColor: "{colors.panel-2}"
    textColor: "{colors.txt}"
    rounded: "{rounded.xl}"
    padding: "10px 14px"
---

# Design System: WispCode User Portal

## Overview

**Creative North Star: "The Problem-Set Console"**

WispCode is a developer workbench where an app is generated, iterated, and shipped — the same job-to-be-done shape as a competitive-programming judge: a list of items, each with a state, each one click from its workspace. The visual world is borrowed deliberately from the LeetCode / 牛客网 canon its users live in daily: flat charcoal (or paper-white) surfaces, hairline borders doing all the structural work, one saturated gold reserved for the primary action and the active marker, and a difficulty-pill vocabulary for state. Density and scanability outrank expression; every screen reads like a problem set, never like a marketing page.

Two themes ship at full fidelity by pure token swap: LeetCode charcoal dark is the default (the untouchable `assets/api.js` applies `data-theme="dark"` on load and the per-page anti-flash script reads the same `wisp_theme` key), and a 牛客-style paper light overrides it under `html[data-theme="light"]`, each with its own `color-scheme`. Every page is a console: a sticky solid nav with a 2px gold underline on the active section, a toolbar row for list metadata and paging, and grids of uniform item cards whose pill row reads state at a glance.

Nothing glows, nothing floats at rest. Depth is tonal layering — inset wells inside panels inside the page — plus 1px hairlines; shadows appear only as a response (light-theme card lift, hover, overlay). The only authored motion is a single 280 ms rise on page entry with a 40 ms child stagger, plus the toast; icons are drawn 2px-stroke line SVGs, never typed. Type is the system CJK workhorse stack at a 14px body, with weight and ramp steps carrying all hierarchy.

**Key Characteristics:**
- Flat matte surfaces, hairline (1px) borders, zero gradients and zero glass.
- One gold accent (#ffa116) carrying every primary action and active marker, with dark ink on filled controls.
- Difficulty-pill state vocabulary: teal tint = deployed/success, neutral tint = idle, red tint = failure.
- Dual theme by token swap: LeetCode charcoal dark (default) and paper light, each with its own `color-scheme`.
- System workhorse type stack with CJK fallbacks; nine-step px ramp 12/13/14/15/16/18/20/22/24, weights 400/600/700.
- Line-drawn 24-viewBox SVG icons at 2px stroke, 16px (14px in small controls, 18px inside tinted tiles); no emoji in chrome.

## Colors

A judging platform's functional palette: one gold accent, one teal verdict, one red danger, one blue reserved for content links, on two neutral worlds that swap wholesale by theme.

### Primary
- **Signal Gold** (#ffa116, both themes): the brand tile, every primary button, the active nav underline, focus rings, text selection, checked radios, and the active step's dot and tint. Filled controls set dark ink on top (#231800 dark / #241700 light, ≥7:1). Hover steps to #ffab30 (dark) / #f59500 (light). Tint family: brand-soft rgba(255,161,22,.12) fills, brand-line rgba(255,161,22,.4) dark / .45 light strokes, selection rgba(255,161,22,.3).

### Secondary
- **Accepted Teal** (#00b8a3 dark / #00a68f light): the deploy/success verdict. Filled deploy buttons set teal with dark ink (#04322c / #05342e); the tint family ok-soft / ok-txt (#3fd0bd / #067a68) / ok-line carries "已部署" pills, ok alerts, green icon tiles, and the done step's dot.

### Tertiary
- **Judge Red** (#d93a40, both themes): cancel-deploy, logout, and every failure. Filled red buttons set white ink (#ffffff, ≥4.5:1); the tint family err-soft / err-txt (#ff8a86 / #c2292f) / err-line carries err alerts and pills. Shipped as-is: the dark tints derive from #e5484d while the light tints derive from #d93a40.

### Content blue
- **Platform Blue** (#57a3f3 dark / #0070d6 light): content links only — body links, markdown links, preview links — hovering to #7fbcff / #005cad. Its tint family info-soft / info-txt (#7fbcff / #0062b8) / info-line carries info alerts and the blue icon tile. It never fills a control and never marks state.

### Neutral
- **Charcoal Field** (dark, default): page #1a1a1a, secondary/badge/table-head #222222, panel & nav #282828, nested panel & AI bubble #2f2f2f, input/markdown well #1f1f1f, hairline #3e3e3e, strong hairline & scrollbar #545454, ink #eff1f5, muted #9ba1a9 (≥4.5:1 on every dark surface).
- **Paper Field** (light): page #f5f6f7, secondary #eceef1, panel & nav #ffffff, nested/well #f7f8fa, hairline #d7dce4, strong hairline #bfc6d1, ink #1f2329, muted #57606c (≥4.5:1).
- **Code neutrals**: code block background #323232 / #f0f1f3; inline code ink #ffb454 / #b8420f.

### Fixed fixtures (intentional, non-token)
- **Toast fixture**: panel #22262c, ink #f2f4f7, hairline rgba(255,255,255,.08), status dots #19c9a9 (ok) / #f4645f (err) — stays dark in both themes because a toast is an overlay above the theme, not part of it.
- **Spinner**: track ring rgba(128,128,128,.4) with a `currentColor` top segment, so it inherits the surrounding muted ink.
- **Zebra rows** in markdown tables: rgba(128,128,128,.05).
- **Press feedback**: inset rgba(0,0,0,.2) on button `:active`.
- **Preview iframe canvas**: #ffffff in both themes (the generated app is not themed by the portal).
- **Mask paints** (#000 inside `mask-image` data URIs and the nav fade gradient) are alpha channels, never visible color.

### Named Rules
**The Gold Restraint Rule.** Gold appears only on primary actions, active markers, and the logo. If a screen shows more than one gold-filled control at rest, one of them should be a ghost.
**The State-Color Rule.** State is always a pill: teal tint = live/success, neutral tint = idle, red tint = failure. State is never conveyed by a bare colored word or a colored card border, and neutral states (未部署) never borrow an alarm color.
**The Fixture Rule.** Toasts, spinner tracks, and zebra rows keep their neutral fixture values in both themes; do not tokenize them into the theme swap.

## Typography

**Display Font:** system CJK workhorse stack (`--font`: -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "Helvetica Neue", Arial, sans-serif)
**Body Font:** the same stack — one family carries the whole console
**Label/Mono Font:** `--mono` (ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono", monospace) for IDs, preview URLs, raw docs, and code only

**Character:** No webfonts, no display face. Hierarchy comes from weight (400/600/700), a tight nine-step px ramp, and slight negative tracking on the two largest headings — the typographic voice of a dense tooling platform that loads instantly on campus networks.

### Hierarchy
- **Display** (700, 24px, 1.6, -0.01em): page titles; drops to 20px at ≤560px.
- **Wordmark** (700, 22px): the auth-page brand line and the large profile avatar initial.
- **Brand** (700, 18px, +0.2px): nav brand wordmark and the profile name — the only on-ramp step between title and body.
- **Title** (700, 16px, -0.01em): card headings, flexed with an 8px icon gap.
- **Item title** (700, 15px, 1.4): app-card names; block buttons also set 15px at weight 600.
- **Body** (400, 14px, 1.6): everything read — paragraphs, controls, tables, chat bubbles; markdown body relaxes to 1.75 line-height.
- **Label** (600, 13px): field labels, card hints, small buttons, md-tabs, back links; hints run 400 muted.
- **Badge** (600, 12px, 1.5): pills and paging counters, always `tabular-nums`.
- **Mono** (13px, 1.7): raw doc wells, fenced code, markdown tables; inline `.mono` runs at .94em.

### Named Rules
**The Ramp-Only Rule.** Every type size on screen is one of 12 / 13 / 14 / 15 / 16 / 18 / 20 / 22 / 24 px (20px only as the mobile page title, 22px only as wordmark/large-avatar); markdown headings scale in em inside the 14px body. No off-ramp sizes.
**The Two-Weight Rule.** Hierarchy comes from weight and 4px-scale size steps, never from color tinting of headings or letter-spaced eyebrows.

## Layout

A single centered column console: `.wrap` caps at 1080px (`.wrap.wide` at 1280px for list pages), padded 32px top / 24px sides / 64px bottom, under a sticky 56px nav (`--nav-h`). Section rhythm is carried by `mt-*` utilities (8/12/16/24/32) and a 4px-multiple spacing scale (4/8/12/16/24/32/48/64) used for card padding (24px, 16px at ≤560px), grid gaps (16px), toolbar padding (12px 16px), and page margins.

Grids: `.grid.cols-2` / `.cols-3` at 16px gap collapse to one column at ≤820px; the app-card grid is `repeat(auto-fill, minmax(272px, 1fr))`, with a 4-column variant stepping 4→3 at ≤1100px, →2 at ≤900px, →1 at ≤560px. At ≤820px the nav wraps to two rows (links drop to their own 40px-tall row with a right-edge fade mask) and the key-value grid narrows its label column 120px→96px (88px at ≤560px). List pages open with a toolbar card (count left, pager right) above the grid; empty and loading states span the full grid width.

## Elevation & Depth

This is a flat matte world: depth is tonal layering (inset wells inside panels inside the page background) plus 1px hairlines that draw every edge. Shadows are responses, never decoration: dark-theme cards carry no shadow at all, light-theme cards get a whisper of lift, hover earns a small shadow plus a 1px rise, and only overlays (auth card, toasts) sit on a deep ambient shadow. There is no blur, no glow, no glass.

### Shadow Vocabulary
- **Card lift, light only** (`0 1px 2px rgba(16,24,40,.05)`): `.card` and `.app-card` in the light theme. The dark theme defines `--shadow-sm` as `0 1px 2px rgba(0,0,0,.3)` but applies nothing at rest.
- **Overlay** (`0 8px 24px rgba(0,0,0,.35)` dark / `0 8px 24px rgba(16,24,40,.12)` light): the auth card and toasts only.
- **Hover lift** (`0 2px 8px rgba(0,0,0,.28)` dark / `0 2px 8px rgba(16,24,40,.08)` light): app-card hover, paired with `translateY(-1px)` and a `line-strong` border.
- **Press inset** (`inset 0 1px 3px rgba(0,0,0,.2)`): button `:active`.

### Named Rules
**The Flat-By-Default Rule.** No surface carries a shadow at rest in the dark theme; elevation is a response to hover or overlay, never decoration.
**The Dark-Stays-Flat Rule.** The light theme's 1px card lift never migrates to dark — charcoal panels are separated by hairlines alone.

## Shapes

A restrained radius ramp on a 2px step: `--r-xs` 4px (inline code), `--r-sm` 6px (small buttons, md-tabs, blockquotes, markdown images, brand tile), `--r-md` 8px (buttons, inputs, alerts, steps, doc/markdown wells, toasts, icon tiles), `--r-lg` 10px (cards, app cards, empty states, large avatar), `--r-xl` 12px (auth card; chat bubbles use the same 12px value), `--r-pill` 999px (badges, scrollbar thumb), and true 50% circles for avatars, step dots, spinners, and toast status dots.

Structure is drawn with 1px hairlines (`--line`), strengthening to `--line-strong` on hover, scrollbars, and the one dashed border in the system — the empty state. Markers are 2px lines: the active nav underline pinned to the bar's bottom edge, and the active md-tab's inset underline (`inset 0 -2px 0` gold). No offset shadows, no clipping shapes, no accent side-bars.

## Components

### Navigation
Solid sticky bar (56px, panel background, 1px bottom hairline). Brand = 28px gold tile (6px radius) with a drawn bolt glyph in brand-ink + 18px wordmark. Links are 14px/600 muted, hover to ink; the active link is ink at 700 with a 2px gold underline inset 12px from each side at the bar's bottom edge. Right side: theme toggle (ghost sm button whose label names the *target* theme — "浅色" in dark, "深色" in light — with a 14px sun/moon mask icon), then either a user chip (30px gold circle avatar with initial + name + ghost 退出 button) or a gold 登录 button. The nav DOM is generated by the untouchable `api.js`; its structure and the toggle's label behavior are fixed.

### Buttons
- **Shape:** gently curved (8px); small variant 6px; block variant full-width at 12px 16px / 15px.
- **Primary:** gold fill with dark ink (9px 16px, 14px/600, 8px icon gap); hover steps gold to its hover token; active presses with an inset shadow; disabled at 45% opacity.
- **Ghost:** panel fill, 1px hairline, ink text; hover deepens to bg-soft with a line-strong border.
- **Ok / Danger:** teal fill with teal-ink (hover brightens 8%) / red fill with white ink.
- **Focus:** 2px gold outline at 2px offset on every variant.

### Badges & state pills
Capsule pills (999px, 12px/600, 2px 10px, tabular-nums): neutral = bg-soft fill with muted ink (IDs, app types, paging); `on` = teal tint fill, teal text, teal hairline (已部署); `off` = neutral fill with a hairline border (未部署 — deliberately calm).

### Steps
Flat boxed strip: each step is a hairline card (8px, 12px padding) with a 26px numbered dot. Idle = muted; active = gold-tinted fill, gold hairline, gold dot with dark-ink number; done = ink text with a teal dot. The overview's how-to strip ships all-idle; active/done states live on the create wizard.

### Cards & item cards
Cards: panel fill, 1px hairline, 10px radius, 24px padding (16px mobile); toolbar cards tighten to 12px 16px. Item (app) cards: 16px padding, column flex with an 8px gap — bold 15px name, 2-line-clamped 13px muted description, pill row, and an actions row pinned to the bottom (`margin-top:auto`). Hover: line-strong border + hover shadow + 1px rise. Card headings take an optional 36px tinted icon tile (orange/green/blue) at 12px gap.

### Inputs & choice cards
Inset wells (inset background, 1px hairline, 8px radius, 10px 12px, 14px) with gold caret; focus swaps the border to gold and adds a 3px brand-soft ring. Labels are 13px/600 muted above. Radio choice cards are full cards: hover strengthens the hairline; `:checked` (via `:has`) tints the card brand-soft with a gold border and a gold-accented radio.

### Segmented controls
Auth tabs: an inset tray (8px radius, 4px padding) holding two equal buttons; active = gold fill with dark ink. Markdown tabs: transparent hairline buttons (6px radius, 13px/600); active = panel fill, line-strong border, 700 weight, and a 2px inset gold underline.

### Alerts & toasts
Alerts: tinted panels (err/ok/info soft fill, matching text and hairline, 8px radius, 14px) that appear with a 16px mask-drawn state icon in the same ink. Toasts: fixed bottom-right stack of dark fixture panels (8px radius, 12px 16px, 14px/600) with an 8px status dot; they slide up 8px on entry and slide right while fading on exit (the exit transition lives in the untouchable `api.js`). At ≤560px toasts span the viewport width.

### Chat
Bubble pairs: user = gold-tinted bubble with gold hairline, right-aligned, gold square avatar (8px radius) with a drawn person mask; AI = panel-2 bubble with hairline, left-aligned, bg-soft avatar with a drawn robot mask. The emoji in the JS templates is silenced (`font-size:0`) and replaced by the mask icons — CSS-only, because the templates are untouchable.

### Avatars & icon system
Avatars: 30px gold circle with dark-ink initial (nav), 56px 10px-radius gold square at 22px (profile). Icons: 24-viewBox line SVGs at 2px stroke, 16px inline (14px in small buttons, badges, and md-tabs; 18px inside 36px tinted tiles), colored by `currentColor`. Where icons must appear inside untouchable JS strings (chat avatars, alert states, theme toggle), CSS `mask-image` data URIs draw them instead.

### Markdown body
LeetCode editorial typography inside an inset well (8px radius, 16px 24px padding, 1.75 line-height): h1/h2 carry a 1px hairline underline; blockquotes are neutral bg-soft fills (6px radius), never colored bars; inline code is a code-bg chip (4px radius) with tinted ink; fenced code sits in its own bordered code-bg panel; tables are hairline-gridded with bg-soft heads and zebra rows; task-list checkboxes take the gold accent.

### Empty & loading states
Empty: dashed line-strong border, 10px radius, 48px vertical padding, centered muted text with an inline link. Loading: muted 14px row with the neutral spinner ring. Both span the full grid width.

### Key-value grid & toolbar
`kv`: two-column grid (120px label / fluid value, 12px 16px gaps), muted keys, break-all values, mono for IDs and URLs. Toolbars: hairline cards with count left, spacer, and pager (ghost sm buttons + pill counter) right; at ≤560px the count wraps to its own row.

## Do's and Don'ts

### Do:
- **Do** draw structure with 1px hairlines and tonal insets; borders are the skeleton of every screen.
- **Do** reserve gold for the single primary action and active markers per screen.
- **Do** express every state as a tinted pill (teal/neutral/red) with matching text and border tints.
- **Do** keep both themes in lockstep: every dark token has a light counterpart swapped via `data-theme`, each theme setting its own `color-scheme`.
- **Do** use line-drawn 24-viewBox SVG icons at 2px stroke, sized 16px (14px in small controls, 18px in tinted tiles).
- **Do** set tabular numerals on every numeric surface (badges, paging, kv, mono).

### Don't:
- **Don't** use gradients, glass, blur, or glow — the world is flat matte surfaces.
- **Don't** add colored side borders or thick accent bars to cards, alerts, or quotes; blockquotes are neutral fills.
- **Don't** let a shadow appear on a dark-theme surface at rest; in light, only the 1px card lift.
- **Don't** introduce a second accent hue; blue is for content links and info only.
- **Don't** set emoji in chrome — icons are drawn, not typed. Emoji arriving inside untouchable JS strings is silenced by CSS where it can be (⚡ logo, 🤖/🧑 chat avatars) or tolerated as content where it cannot (👋 greeting, 👤 owner badge, ▶/⏹ deploy label); neither case is house style.
- **Don't** alarm a neutral state: 未部署 stays a gray pill, never red.
- **Don't** step off the type ramp (12–24px) or the radius ramp (4/6/8/10/12/999/50%).
