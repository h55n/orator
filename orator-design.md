---
version: alpha
name: orator
description: A bright, modern SaaS system with expressive purple branding, airy spacing, and a playful security-focused tone.
colors:
  primary: "#6D4AE3"
  primary-60: "#8D75E6"
  primary-20: "#E9E1FF"
  secondary: "#F47E3F"
  tertiary: "#5F3FC4"
  neutral: "#FCF9F5"
  surface: "#FFFFFF"
  on-surface: "#5B47B6"
  on-primary: "#FFFFFF"
  border: "#D7CCFF"
  success: "#1F7A57"
  error: "#D94C3D"
typography:
  headline-display:
    fontFamily: Reckless
    fontSize: 58px
    fontWeight: 400
    lineHeight: 70px
    letterSpacing: -1.45px
  headline-lg:
    fontFamily: Reckless
    fontSize: 42px
    fontWeight: 400
    lineHeight: 50px
    letterSpacing: -0.76px
  headline-md:
    fontFamily: Reckless
    fontSize: 30px
    fontWeight: 400
    lineHeight: 36px
    letterSpacing: 0px
  headline-sm:
    fontFamily: Reckless
    fontSize: 22px
    fontWeight: 400
    lineHeight: 26px
    letterSpacing: -0.52px
  body-lg:
    fontFamily: Roobert
    fontSize: 18px
    fontWeight: 400
    lineHeight: 28px
    letterSpacing: -0.16px
  body-md:
    fontFamily: Roobert
    fontSize: 16px
    fontWeight: 400
    lineHeight: 22px
    letterSpacing: -0.16px
  body-sm:
    fontFamily: Roobert
    fontSize: 14px
    fontWeight: 400
    lineHeight: 20px
    letterSpacing: -0.12px
  label-lg:
    fontFamily: Roobert
    fontSize: 16px
    fontWeight: 600
    lineHeight: 20px
    letterSpacing: 0px
  label-md:
    fontFamily: Roobert
    fontSize: 14px
    fontWeight: 600
    lineHeight: 20px
    letterSpacing: 0px
  label-sm:
    fontFamily: Roobert
    fontSize: 12px
    fontWeight: 700
    lineHeight: 16px
    letterSpacing: 0.04em
  nav-link:
    fontFamily: Roobert
    fontSize: 14px
    fontWeight: 700
    lineHeight: 20px
    letterSpacing: 0px
  button:
    fontFamily: Roobert
    fontSize: 14px
    fontWeight: 600
    lineHeight: 18px
    letterSpacing: 0.04em
  caption:
    fontFamily: Roobert
    fontSize: 12px
    fontWeight: 500
    lineHeight: 16px
    letterSpacing: 0px
rounded:
  none: 0px
  sm: 4px
  md: 7px
  lg: 12px
  xl: 24px
  full: 9999px
spacing:
  xs: 6px
  sm: 14px
  md: 24px
  lg: 34px
  xl: 168px
  gutter: 24px
  section: 168px
components:
  button-primary:
    backgroundColor: "{colors.neutral}"
    textColor: "{colors.on-surface}"
    typography: "{typography.button}"
    rounded: "{rounded.md}"
    padding: 12px 14px
    height: 39px
  button-primary-hover:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.tertiary}"
    typography: "{typography.button}"
    rounded: "{rounded.md}"
    padding: 12px 14px
    height: 39px
  button-secondary:
    backgroundColor: "transparent"
    textColor: "{colors.on-primary}"
    typography: "{typography.button}"
    rounded: "{rounded.md}"
    padding: 12px 14px
    height: 39px
  button-tertiary:
    backgroundColor: "transparent"
    textColor: "{colors.primary}"
    typography: "{typography.nav-link}"
    rounded: "{rounded.none}"
    padding: 0px
  card:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.success}"
    rounded: "{rounded.xl}"
    padding: 24px
  input:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.on-surface}"
    rounded: "{rounded.md}"
    padding: 12px 14px
  chip:
    backgroundColor: "{colors.primary-20}"
    textColor: "{colors.on-surface}"
    rounded: "{rounded.full}"
    padding: 6px 12px
  badge:
    backgroundColor: "{colors.secondary}"
    textColor: "{colors.on-primary}"
    rounded: "{rounded.full}"
    padding: 4px 10px
---

# Tines Editorial

## Overview
Tines feels playful, confident, and highly polished, balancing enterprise trust with a friendly, creative voice. The page is spacious and visually airy, with large editorial headlines, bold accent color, and a strong sense of motion and personality. It is clearly aimed at security, IT, and operations teams, but the tone avoids cold enterprise austerity in favor of warmth and approachability.

## Colors
- **Primary (#6D4AE3):** The signature purple used for navigation, headings, links, and core brand emphasis. It carries most of the interface identity and should remain the default action and brand color.
- **Primary 60 (#8D75E6):** A lighter lavender-purple used for softer UI moments, secondary emphasis, and subtle background treatments.
- **Primary 20 (#E9E1FF):** A very pale lilac used for chips, selected states, and quiet surfaces that need brand tint without strong contrast.
- **Secondary (#F47E3F):** A vivid orange accent that appears in highlights and celebratory details. Use it sparingly to draw attention to key callouts or contrasting badges.
- **Tertiary (#5F3FC4):** A deeper violet for hover states and stronger purple accents when more depth is needed than the primary alone.
- **Neutral (#FCF9F5):** The warm off-white page base. It keeps the UI soft and editorial instead of stark white.
- **Surface (#FFFFFF):** Pure white used for cards, inputs, and elevated content areas.
- **On Surface (#5B47B6):** A saturated purple text tone for readable UI text on light surfaces, especially supporting labels and secondary copy.
- **On Primary (#FFFFFF):** White text for filled purple buttons and dark brand surfaces.
- **Border (#D7CCFF):** A light lavender border color for outlined controls and subtle separation.
- **Success (#1F7A57):** A calm green used in card examples and success-oriented content.
- **Error (#D94C3D):** A reserved alert color for validation and destructive states.

## Typography
The system pairs a serif display family, Reckless, with a geometric sans family, Roobert. Reckless is used for headlines and gives the site its editorial, premium feel; it should stay light in weight and use slightly tightened tracking, especially at large sizes. Roobert handles body copy, navigation, buttons, and labels with clearer utility and a modern SaaS tone.

Use `headline-display` for hero statements, `headline-lg` and `headline-md` for major section titles, and `headline-sm` for compact but expressive headings. Body text should default to `body-md`, with `body-lg` for lead paragraphs and `body-sm` for supporting metadata. Labels and controls should use `label-md` or `label-lg`, while `label-sm` is appropriate for small badges, uppercase-feeling utility text, and fine-grained UI annotations. The source shows compact, confident letter spacing in buttons and labels, but no heavy uppercase system; when caps are needed, keep them subtle and spacious.

## Layout
The layout is wide, centered, and generous, with a strong left-aligned content column in the hero and large vertical breathing room between sections. The structure behaves like a fluid marketing grid rather than a strict dashboard grid, but the content still feels disciplined because of consistent alignment and repeated spacing intervals. Use `section` spacing for major vertical breaks, `md` and `lg` for module separation, and `xs` to `sm` for internal control gaps.

Cards and embedded product previews should sit in roomy containers with substantial internal padding and rounded corners. The general rhythm is open and premium: avoid dense stacking, and prefer fewer elements per row unless the content is clearly supporting the hero narrative.

## Elevation & Depth
The visual system is intentionally flat. There are no meaningful shadows; hierarchy comes from color contrast, size, borders, and spacing rather than elevation. Surface containers are distinguished with white fills, pale borders, and soft lavender backgrounds, while action buttons use saturated fills to stand out without casting depth.

Because the system is flat, use tonal layering carefully: off-white page backgrounds, white cards, and light purple panel fills should create separation before any shadow is considered. If a component needs emphasis, increase contrast or border clarity rather than introducing heavy drop shadows.

## Shapes
The shape language is friendly and rounded without becoming bubbly. Buttons use a small radius around `md`, while larger cards move up to `xl` for a softer, more organic presentation. Pills and badges should be fully rounded, reinforcing the playful brand energy and the lightweight feel of navigation and chips.

Overall, the geometry should feel approachable and modern: no sharp corners on major actions, but also no overly exaggerated curvature on primary containers.

## Components
**Buttons:** Primary buttons should use `button-primary` with a light filled background, purple text, medium weight Roobert, and compact padding. Secondary buttons should use `button-secondary` with transparent fill and a visible border treatment, preserving the same size and type scale. Tertiary buttons and nav-style actions should feel like links, using `button-tertiary` with minimal chrome and no forced container shape. Hover states should deepen toward `colors.tertiary` or shift to a cleaner surface contrast, but keep the motion subtle and restrained.

**Cards:** Cards should feel calm and content-forward, with `card` using a white surface, generous padding, and `rounded.xl`. Borders can be soft or omitted if the surrounding background already provides enough separation. Cards should never feel heavy; they are framing devices for product previews and supporting information.

**Inputs:** Inputs should be white, softly rounded, and lightly bordered or implied through spacing. Use `input` for text fields, search fields, and form controls. Focus states should favor purple border and text emphasis rather than shadows.

**Chips and badges:** Chips should use pale lavender fills with rounded pills and compact padding. Badges can use the orange accent or brand purple for quick attention, but they should remain small and concise.

**Navigation:** Top navigation links are plain, medium-small Roobert text with strong brand purple color. Active or hover states should be understated; avoid heavy underlines or blocks. The demo CTA in the header should remain the most prominent control.

**Illustrative content blocks:** Large graphic or media components should be allowed to dominate visually, but they should still inherit the system’s soft radii, generous margins, and brand colors. Keep framing minimal so the artwork remains the hero.

## Do's and Don'ts
- Do use Reckless for emotional, editorial headlines and Roobert for everything functional.
- Do keep layouts airy with large vertical spacing and generous side margins.
- Do rely on color and scale for hierarchy instead of shadows or layered elevation.
- Do use rounded pills for badges and small controls, and medium rounding for buttons.
- Don't introduce dark, heavy surfaces or dense enterprise-style panels.
- Don't use stark black text or pure grayscale as the dominant palette when purple brand tones are available.
- Don't overuse the orange accent; reserve it for highlights and attention moments.
- Don't make controls feel bulky or squared-off; the system should stay soft and approachable.