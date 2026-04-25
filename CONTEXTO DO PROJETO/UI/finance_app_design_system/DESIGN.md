---
name: Finance App Design System
colors:
  surface: '#FFFFFF'
  surface-dim: '#d8dbd2'
  surface-bright: '#f7fbf1'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f5ec'
  surface-container: '#ecefe6'
  surface-container-high: '#e6e9e0'
  surface-container-highest: '#e0e4db'
  on-surface: '#1C1B1F'
  on-surface-variant: '#41493e'
  inverse-surface: '#2d322c'
  inverse-on-surface: '#eff2e9'
  outline: '#717a6d'
  outline-variant: '#c0c9bb'
  surface-tint: '#2a6b2c'
  primary: '#00450d'
  on-primary: '#FFFFFF'
  primary-container: '#C8E6C9'
  on-primary-container: '#00600F'
  inverse-primary: '#91d78a'
  secondary: '#9e4200'
  on-secondary: '#FFFFFF'
  secondary-container: '#FFE0B2'
  on-secondary-container: '#E65100'
  tertiary: '#0277BD'
  on-tertiary: '#FFFFFF'
  tertiary-container: '#B3E5FC'
  on-tertiary-container: '#01579B'
  error: '#B00020'
  on-error: '#FFFFFF'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#acf4a4'
  primary-fixed-dim: '#91d78a'
  on-primary-fixed: '#002203'
  on-primary-fixed-variant: '#0c5216'
  secondary-fixed: '#ffdbcb'
  secondary-fixed-dim: '#ffb691'
  on-secondary-fixed: '#341100'
  on-secondary-fixed-variant: '#793100'
  tertiary-fixed: '#ffd9e2'
  tertiary-fixed-dim: '#ffb1c8'
  on-tertiary-fixed: '#3e001d'
  on-tertiary-fixed-variant: '#7a2949'
  background: '#FAFAFA'
  on-background: '#1C1B1F'
  surface-variant: '#e0e4db'
  dark-primary: '#81C784'
  dark-background: '#121212'
  dark-surface: '#1E1E1E'
typography:
  display-lg:
    fontFamily: Work Sans
    fontSize: 57px
    fontWeight: '700'
    lineHeight: 64px
  display-md:
    fontFamily: Work Sans
    fontSize: 45px
    fontWeight: '700'
    lineHeight: 52px
  display-sm:
    fontFamily: Work Sans
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
  headline-lg:
    fontFamily: Work Sans
    fontSize: 32px
    fontWeight: '400'
    lineHeight: 40px
  headline-md:
    fontFamily: Work Sans
    fontSize: 28px
    fontWeight: '400'
    lineHeight: 36px
  headline-sm:
    fontFamily: Work Sans
    fontSize: 24px
    fontWeight: '400'
    lineHeight: 32px
  title-lg:
    fontFamily: Work Sans
    fontSize: 22px
    fontWeight: '500'
    lineHeight: 28px
  title-md:
    fontFamily: Work Sans
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
  title-sm:
    fontFamily: Work Sans
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
  body-lg:
    fontFamily: Work Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Work Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Work Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Work Sans
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
  label-md:
    fontFamily: Work Sans
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  label-sm:
    fontFamily: Work Sans
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  xxs: 4px
  xs: 8px
  s: 12px
  m: 16px
  l: 24px
  xl: 32px
  xxl: 48px
---

🎨 TEMA & DESIGN TOKENS
Cores Principais
kotlin// Light Theme
Primary: #1B5E20          // Verde escuro (confiança financeira)
OnPrimary: #FFFFFF        // Texto em cima do primary
PrimaryContainer: #C8E6C9 // Verde claro para cards
OnPrimaryContainer: #00600F

Secondary: #FF6F00        // Laranja (alertas/ações)
OnSecondary: #FFFFFF
SecondaryContainer: #FFE0B2
OnSecondaryContainer: #E65100

Tertiary: #0277BD         // Azul (metas/futuro)
OnTertiary: #FFFFFF
TertiaryContainer: #B3E5FC
OnTertiaryContainer: #01579B

Error: #B00020           // Vermelho para despesas/erros
OnError: #FFFFFF

Background: #FAFAFA      // Cinza muito claro
OnBackground: #1C1B1F

Surface: #FFFFFF         // Branco puro para cards
OnSurface: #1C1B1F

// Dark Theme (opcional)
Primary: #81C784
Background: #121212
Surface: #1E1E1E
Tipografia
Display Large: Roboto 57sp, Bold
Display Medium: Roboto 45sp, Bold
Display Small: Roboto 36sp, Bold

Headline Large: Roboto 32sp, Regular
Headline Medium: Roboto 28sp, Regular
Headline Small: Roboto 24sp, Regular

Title Large: Roboto 22sp, Medium
Title Medium: Roboto 16sp, Medium
Title Small: Roboto 14sp, Medium

Body Large: Roboto 16sp, Regular
Body Medium: Roboto 14sp, Regular
Body Small: Roboto 12sp, Regular

Label Large: Roboto 14sp, Medium
Label Medium: Roboto 12sp, Medium
Label Small: Roboto 11sp, Medium
Espaçamentos Padrão
XXS: 4dp
XS: 8dp
S: 12dp
M: 16dp
L: 24dp
XL: 32dp
XXL: 48dp
Border Radius
Small: 8dp   (botões, chips)
Medium: 12dp (cards pequenos)
Large: 16dp  (cards grandes)
XLarge: 28dp (FAB, dialogs)
Elevações (Shadow)
Level 0: 0dp (flat)
Level 1: 1dp (cards sutis)
Level 2: 3dp (cards elevados)
Level 3: 6dp (FAB, bottom sheets)
Level 4: 8dp (navigation drawer)
Level 5: 12dp (dialogs)