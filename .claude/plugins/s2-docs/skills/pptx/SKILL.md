---
name: pptx
description: "This skill should be used when the user asks to create a presentation (.pptx), slide deck, workshop deck, pitch deck, or any slides with S2GRUPO corporate branding (blue #0545b0, white logo, claim 'Anticipando un mundo ciberseguro'). Also triggers when the user needs dark terminal/console blocks, animated code snippets, card layouts, comparison tables, or technical content for developer audiences in presentation format. Produces polished .pptx files with line-by-line terminal animations, syntax-highlighted code, colored accent cards, and proper S2GRUPO identity on every slide."
---

# S2 Presentations — PPTX Skill

Create professional, branded S2GRUPO presentations with rich visual elements: animated terminal blocks, syntax-highlighted code, card grids, tables, and corporate identity.

> Brand source of truth: skill `s2-docs:branding`

## Quick Reference

| Task | What to do |
|------|-----------|
| Create a new presentation | Read this file, adapt `templates/build_presentation.js`, run, inject animations |
| Understand the visual system | Read [references/design-system.md](references/design-system.md) |
| Add terminal animations | Run `scripts/inject_animations.py` after building the PPTX |

## Architecture

Two-step pipeline:

```
1. Node.js (pptxgenjs)     →  Generates .pptx with shapes, text, terminals, cards, tables
2. Python (python-pptx)    →  Post-processes to inject Appear animations on terminal lines
```

**Why two steps?** pptxgenjs excels at creating slides with precise positioning but has zero animation support. python-pptx can inject `<p:timing>` animation nodes but is cumbersome for building layouts from scratch.

## How to Use

### Step 1: Copy logos and template

```bash
cp ${CLAUDE_PLUGIN_ROOT}/skills/pptx/assets/logo_blue.b64 ./
cp ${CLAUDE_PLUGIN_ROOT}/skills/pptx/assets/logo_white.b64 ./
cp ${CLAUDE_PLUGIN_ROOT}/skills/pptx/templates/build_presentation.js ./build.js
# Edit build.js to match presentation content
node build.js
```

The logos are bundled in the skill's `assets/` directory — always copy them to the working directory before running the build script.

The template is a **working example** — copy, read, and modify. Key sections:
1. **Slide content** — the `// SLIDE N:` blocks at the bottom
2. **Section structure** — add/remove `addSectionSlide()` calls
3. **Terminal content** — modify `addTerminal()` line arrays
4. **Cards and tables** — use `addCard()` and `slide.addTable()`

### Step 3: Inject animations

```bash
python ${CLAUDE_PLUGIN_ROOT}/skills/pptx/scripts/inject_animations.py \
  --input output.pptx --meta anim_meta.json --output output-animated.pptx
```

### Step 4: QA

```bash
soffice --headless --convert-to pdf output-animated.pptx
pdftoppm -jpeg -r 150 output-animated.pdf slide
```

## Visual Components

### Slide Types

| Function | Background | Use for |
|----------|-----------|---------|
| `addBlueSlide()` | S2 Blue (`#0545b0`) | Title, section dividers, closing |
| `addWhiteSlide(title, subtitle?)` | White + blue title bar | All content slides |
| `addSectionSlide(tag, title, subtitle)` | S2 Blue + tag badge | Block/chapter dividers |

### Content Elements

| Function | Description |
|----------|------------|
| `addTerminal(slide, x, y, w, h, title, lines)` | Dark terminal with dots, header, colored syntax lines (7pt Consolas, autoFit). Each line is a separate shape for animation. |
| `addCard(slide, x, y, w, h, accentColor, content)` | White card with left accent bar, shadow, and rich text (autoFit). **Default accent = `BLUE`.** Only use `RED`/`GREEN` for explicit before/after or good/bad comparisons. |
| `addParagraph(slide, text, opts?)` | Body text with autoFit. Returns next Y for chaining. Options: `{ x, y, w, h, fontSize, color, lineSpacing, align }`. |
| `slide.addTable(rows, opts)` | Standard pptxgenjs table with S2 blue header row. |

### Layout Constants

Use the named constants for consistent positioning:

```javascript
CONTENT_X  // 0.4"  — left margin
CONTENT_W  // 9.2"  — full-width
CONTENT_Y  // 1.15" — below title bar
COL_W      // 4.3"  — two-column width
COL_R_X    // 5.1"  — right column X
BOX_MARGIN // [8,6,8,6] — internal padding [top,right,bottom,left] in pt for text in boxes
BOX_MIN_H  // 0.55" — minimum box height to fit text + padding
TERM_FONT_SIZE  // 7pt
TERM_LINE_H     // 0.15"
```

### Terminal Line Format

```javascript
"plain text here"           // Simple text
""                          // Empty spacer (no animation)
[                           // Rich colored segments
  { text: "$ ", color: TERM_GREEN },
  { text: "claude", color: TERM_TEXT, bold: true }
]
// Colors: TERM_GREEN, TERM_BLUE, TERM_PURPLE, TERM_ORANGE,
//         TERM_RED, TERM_CYAN, TERM_DIM, TERM_TEXT
```

## Card Color Rules

The primary visual identity of S2GRUPO presentations is **blue + yellow/lime**, not red/green.

| Context | Accent Color |
|---------|-------------|
| Default / general content | `BLUE` |
| Agenda items, sessions, blocks | `BLUE` |
| Secondary variety (white bg) | `AMBER` |
| Secondary variety (blue bg) | `LIME` |
| Explicit "before" / bad / incorrect | `RED` |
| Explicit "after" / good / correct | `GREEN` |
| Status dashboard levels | `SEM_*` tokens |

**Never use `RED` or `GREEN` for neutral content** like agenda items, session cards, or general information boxes. When in doubt, use `BLUE`.

## Dependencies

```bash
npm install pptxgenjs              # Node.js
pip install python-pptx lxml       # Python
# System (QA): LibreOffice (soffice) + Poppler (pdftoppm)
```

## File Structure

```
pptx/
├── SKILL.md                          ← this file
├── assets/
│   ├── logo_blue.b64                 ← S2GRUPO blue logo (base64-encoded PNG)
│   └── logo_white.b64                ← S2GRUPO white logo (base64-encoded PNG)
├── templates/
│   └── build_presentation.js         ← working example to copy and adapt
├── scripts/
│   └── inject_animations.py          ← animation post-processor
└── references/
    └── design-system.md              ← colors, spacing, typography rules
```
