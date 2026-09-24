---
version: 1
slug: "rvice-src-main-resources-static-wisp-user-2e7efcf1"
primary_target: "nexus-portal/nexus-portal-service/src/main/resources/static/wisp/user"
related_targets: []
---

# Surface brief: /wisp/user portal (7 pages)

Scope: whole static portal (index, login, create, apps, app, deployed, profile). Visitor mode: Operate — users come to generate, preview, iterate and deploy AI-built apps. Audience: Chinese developers/students, same crowd as LeetCode CN and Nowcoder. Constraints (user-pinned, hard): no component removal; api.js and all inline <script> blocks untouched; every id/class the JS touches must survive; dark is the default theme because api.js applies data-theme="dark" and cannot change.

## Direction contract

THESIS: A generation console that reads like a competitive-programming platform's workspace — dense, flat, list-like, status-colored — refusing the incumbent's gradient-glass "AI SaaS" glow. The product's own vocabulary (steps, badges, deploy state, chat log) is styled in the grammar of a problem-set workspace.

OWN-WORLD: LeetCode/Nowcoder palette at full commitment. Charcoal dark world (#1a1a1a page, #282828 panels, #3e3e3e hairlines, #eff1f5 ink) mirroring LeetCode dark mode; white light world (#f5f6f7 page, #fff panels, #e0e3e8 hairlines). One brand accent — LeetCode orange #ffa116 with dark ink on top — carries CTAs, active nav underline, focus rings, selection, checked radios. Accepted-teal #00b8a3 (LeetCode's green ≈ Nowcoder's brand teal) owns deploy/success; #d93a40 owns danger with white ink; a muted blue #0070d6/#57a3f3 serves content links only, as on both reference sites. Flat 1px-bordered cards, 8–10px radii, zero gradients, zero glass, no halo shadows; pill badges in difficulty-tag colors; system CJK stack; 13/14/16/24 type scale; mono only for raw doc/code.

STORY: Visitor lands on the overview, sees the four-step generation ritual and recent apps as problem-list rows; believes this is a serious tooling platform, not a toy; acts by hitting the orange "开始生成应用" and later the teal deploy button. Every state (loading, empty, error, deployed/not) reads at a glance through the status colors they already know from刷题 platforms.

FIRST VIEWPORT (index, 1440px): 56px solid nav — orange-tile ⚡ brand, five links, active "概览" carrying a 2px orange underline at the bar's bottom edge, theme toggle + avatar chip right. Below, on charcoal: "欢迎回来 👋" 24px bold, muted subtitle, then the flat bordered "如何使用" card with the numbered 4-step strip (active step orange-ringed) and the single orange primary CTA beside one ghost secondary. Then three flat feature cards, then "我的应用" card whose rows are problem-list-style app cards: bold name, muted desc, status pill row (#id, type, 已部署 teal / 未部署 gray), small action buttons anchored bottom.

FORM: User-pinned canon — LeetCode CN + Nowcoder, named by the user; no seed roll (brief-pinned direction beats the roll). Craft bar: leetcode.cn's flat bordered cards, status-color language, and dark-mode charcoal; nowcoder.com's light segmented controls and dense list discipline.

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance

Unresolved: none — dark default forced by api.js is accepted; both themes ship at full fidelity.
