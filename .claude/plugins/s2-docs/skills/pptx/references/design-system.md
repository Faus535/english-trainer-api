# S2GRUPO Presentation Design System

> Brand source of truth: skill `s2-docs:branding`

## Brand Colors

| Token | Hex | Usage |
|-------|-----|-------|
| `BLUE` | `0545b0` | Primary brand, title bars, section backgrounds, accent |
| `BLUE_LIGHT` | `5a84ce` | Secondary accent, highlights, hover states |
| `WHITE` | `FFFFFF` | Content slide backgrounds, text on blue |
| `CREAM` | `ededeb` | Quote boxes, callout backgrounds, light cards |
| `LIME` | `dbf266` | Accent on solid backgrounds only (never on white) |
| `TEXT` | `1A1A2E` | Primary body text |
| `TEXT_DIM` | `5A5A7A` | Secondary/muted text |
| `GREEN` | `1A9A5A` | Positive, success, "after" |
| `AMBER` | `C48800` | Warning, caution, human review steps |
| `RED` | `CC2233` | Error, critical, "before", limitations |
| `CYAN` | `0077AA` | Code highlights, links |

### Semaphoric Colors (strict good/bad context only)

| Token | Hex | Usage |
|-------|-----|-------|
| `SEM_GREEN` | `08ad66` | OK / low risk |
| `SEM_YELLOW` | `e8ed59` | Attention |
| `SEM_ORANGE` | `ea9b31` | Medium alert |
| `SEM_RED` | `c2262e` | Critical |

> **IMPORTANT — Color Usage Rules:**
>
> 1. **Default card accent = `BLUE`**. Most cards, agenda items, session blocks,
>    and general content must use `BLUE` (or `BLUE_LIGHT`) as their accent color.
> 2. **Secondary accent = `LIME`** (on blue backgrounds) or **`AMBER`** (on white
>    backgrounds) for visual variety when you need a second color that is not blue.
> 3. **`GREEN` and `RED` are ONLY for explicit good/bad, before/after, or
>    correct/incorrect comparisons.** Never use them for general-purpose cards,
>    agenda items, session blocks, or neutral content.
> 4. **Semaphoric colors** (`SEM_*`) are reserved exclusively for status
>    dashboards or risk-level indicators where the red/yellow/green mapping
>    carries clear semantic meaning.
>
> When in doubt, use `BLUE`. The primary palette of the presentations is
> **blue + yellow/lime**, not red/green.

## Terminal Colors (Dark Theme)

| Token | Hex | Usage |
|-------|-----|-------|
| `TERM_BG` | `0D1117` | Terminal body background |
| `TERM_HEADER` | `161B22` | Terminal header bar |
| `TERM_GREEN` | `3FB950` | Prompts (`$`, `❯`), success (`✓`) |
| `TERM_BLUE` | `58A6FF` | Spinners (`⠋`), info |
| `TERM_PURPLE` | `BC8CFF` | File/directory paths |
| `TERM_ORANGE` | `D29922` | Warnings (`⚠`), human review |
| `TERM_RED` | `F85149` | Errors (`✗`, `FAILED`) |
| `TERM_CYAN` | `39C5CF` | Arguments, highlights, URLs |
| `TERM_DIM` | `8B949E` | Comments, dim annotations |
| `TERM_TEXT` | `E6EDF3` | Default terminal text |

## Typography

| Element | Font | Size | Weight | autoFit |
|---------|------|------|--------|---------|
| Slide title (blue bar) | Arial | 16pt | Bold | Yes |
| Bar subtitle (right) | Arial | 10pt | Normal | Yes |
| Section title (blue slide) | Arial | 32pt | Bold | Yes |
| Section subtitle | Arial | 14pt | Normal | Yes |
| Body text (`addParagraph`) | Arial | 10pt | Normal | Yes |
| Code / terminal | Consolas | 7pt | Normal | Yes (fallback) |
| Card content | Arial | 9pt | Normal | Yes |
| Table header | Arial | 9pt | Bold, white on blue | No |
| Footer | Arial | 6pt | Normal | No |
| Claim | Arial | 7pt | Italic | No |

> **autoFit** — When enabled, PowerPoint shrinks the font automatically if text
> overflows the shape boundaries. All text-heavy elements use autoFit as a safety
> net. Code in terminals uses 7pt as a base and autoFit as a last resort.

## Layout Grid (10" x 5.625" / 16:9)

```
┌──────────────────────────────────────────────┐
│ [Logo 0.35,0.18]          [Claim right-align]│  ← Header: 0.18" from top
│──────────────────────────────────────────────│  ← Blue bar: y=0.55, h=0.48"
│ Title text (autoFit)       [subtitle autoFit]│
│──────────────────────────────────────────────│
│                                              │
│  Content area: x=0.4, y=1.15                 │  ← Main content starts here
│  Width: 9.2" (two cols: 4.3" + 4.3" gap 0.5)│
│                                              │
│                        [Footer right-align]  │  ← Footer: y=5.3"
└──────────────────────────────────────────────┘
```

### Named Constants (build_presentation.js)

| Constant | Value | Description |
|----------|-------|-------------|
| `CONTENT_X` | 0.4" | Left margin for all content |
| `CONTENT_W` | 9.2" | Full-width content area |
| `CONTENT_Y` | 1.15" | Content start Y (below title bar) |
| `COL_W` | 4.3" | Two-column width |
| `COL_GAP` | 0.5" | Gap between columns |
| `COL_R_X` | 5.1" | Right column X position |
| `BOX_MARGIN` | [8,6,8,6] | Internal padding for text inside boxes (pt: top,right,bottom,left) |
| `BOX_MIN_H` | 0.55" | Minimum height for boxes with text (ensures padding fits) |
| `TERM_FONT_SIZE` | 7pt | Terminal code font size |
| `TERM_LINE_H` | 0.15" | Terminal line height |

### Column Layouts

| Layout | Columns | x positions | Widths |
|--------|---------|-------------|--------|
| Two-col | Left / Right | 0.4 / 5.1 | 4.3" / 4.3" |
| Three-col | L / C / R | 0.25 / 3.45 / 6.65 | 2.95" each |

## Component Specs

### Terminal Block

```
┌─────────────────────────────────────┐
│ ● ● ●  title text                   │ ← Header: h=0.28", bg=TERM_HEADER
│─────────────────────────────────────│
│ $ command                           │ ← Body: bg=TERM_BG
│ ⠋ Loading...                        │    Line height: 0.15"
│ ✓ Success                           │    Padding: x+0.14, w-0.28
│ ✗ Error                             │    Font: Consolas 7pt, autoFit
└─────────────────────────────────────┘
  Corner radius: 0.06", Shadow: blur=4, offset=2, opacity=10%
  Dots: r=0.045", spaced at x+0.12, +0.25, +0.38
```

#### Terminal capacity (9.2" wide terminal)

| Metric | Value |
|--------|-------|
| Usable text width | 8.92" (9.2 - 0.28 padding) |
| Max chars per line (7pt Consolas) | ~110 characters |
| Max lines (h=4.0") | ~24 lines |
| Max lines (h=3.8") | ~23 lines |
| Max lines (h=3.5") | ~21 lines |
| Max lines (h=2.5") | ~14 lines |

> **Tip**: Keep Java/code lines under 90 characters for comfortable readability.
> Lines up to ~110 chars fit physically but look dense. autoFit kicks in as a
> last resort if a line still overflows.

### Card with Accent Bar

```
┌────────────────────────────────────┐
│                                    │  ← 8pt top padding (BOX_MARGIN)
█  Title text                        │  ← Accent bar: w=0.04", full height
█  Description text                  │     Color varies by context
│                                    │     Border: #E0E4EA, 0.5pt
│                                    │  ← 8pt bottom padding
└────────────────────────────────────┘
  Min height: 0.55" (BOX_MIN_H). Shadow: blur=4, offset=2, opacity=10%.
  No corner radius (RECTANGLE).
  Text box uses PHYSICAL offsets (not just margin) to guarantee padding:
    textX = x + 0.16   textY = y + 0.06
    textW = w - 0.30   textH = cardH - 0.12
  margin: [2,2,2,2]pt — tiny safety net only (autoFit can eat margins).
```

> **Padding rule**: Never rely solely on `margin` for padding inside shapes
> with `autoFit: true`. PowerPoint may shrink margins when auto-fitting text.
> Always use **physical offsets** (inset x/y, reduced w/h) as the primary
> padding mechanism, with a small `margin` as a safety net only.

### Blue Section Slide

```
┌──────────────────────────────────────────────┐
│ [White logo]              [Claim]            │
│  ┌──────────┐                                │
│  │ Tag text │  ← Rounded rect, 15% white     │  y=1.3
│  └──────────┘     with 25% white border      │
│  Title Line 1                                │  ← 32pt bold white, y=1.7
│  Subtitle text                               │  ← 14pt, 25% transparent, y=3.4
│                       [Footer]               │
└──────────────────────────────────────────────┘
  Background: #0545b0
```

## Animation System

Terminal lines use **Appear** (presetID=1) effects:
- First line: triggered **on click**
- Subsequent lines: **afterEffect** with cumulative 400ms delays
- Lines are individual text boxes with `objectName: "termline_N"`
- Metadata exported to `anim_meta.json` for the Python post-processor

## Common Patterns

| Pattern | Description |
|---------|------------|
| Before/After | Two card columns: RED accent (left) / GREEN accent (right) + quote box below. **This is the only pattern where RED/GREEN accents are appropriate.** |
| Numbered Steps | Blue OVAL (0.28" dia) with white number + bold title + dim description |
| Warning Box | Rounded rect, fill `FFF3F3`, line RED 0.5pt, red bold title |
| Quote/Callout | Rounded rect, fill CREAM, rectRadius 0.04, centered text |
| Summary Row | Full-width rounded rect at bottom, colored bold keywords separated by `·` |

> **Box padding rule**: Any shape that contains text must use `h >= BOX_MIN_H`.
> For shapes with `autoFit: true`, use **physical offsets** (inset x/y, reduced
> w/h) as the primary padding — do not rely solely on `margin`, which PowerPoint
> may shrink during auto-fitting. Keep `margin` small (2-4pt) as a safety net only.
