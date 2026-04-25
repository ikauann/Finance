# 🎨 UI SPECIFICATIONS - Finance App
## Para Google Stitch / Figma / Design System

> **Objetivo:** Especificações completas para geração automática de todas as telas do app de finanças.
> **Design System:** Material Design 3 (Material You)
> **Plataforma:** Android Mobile (portrait)

---

## 🎨 TEMA & DESIGN TOKENS

### Cores Principais
```kotlin
// Light Theme
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
```

### Tipografia
```
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
```

### Espaçamentos Padrão
```
XXS: 4dp
XS: 8dp
S: 12dp
M: 16dp
L: 24dp
XL: 32dp
XXL: 48dp
```

### Border Radius
```
Small: 8dp   (botões, chips)
Medium: 12dp (cards pequenos)
Large: 16dp  (cards grandes)
XLarge: 28dp (FAB, dialogs)
```

### Elevações (Shadow)
```
Level 0: 0dp (flat)
Level 1: 1dp (cards sutis)
Level 2: 3dp (cards elevados)
Level 3: 6dp (FAB, bottom sheets)
Level 4: 8dp (navigation drawer)
Level 5: 12dp (dialogs)
```

---

## 📱 TELA 1: DASHBOARD (Home)

### Layout Structure
```
┌─────────────────────────────────────────┐
│ [TopAppBar]                      [Goals]│ 56dp
├─────────────────────────────────────────┤
│                                         │
│  💰 Resumo Financeiro                   │ Headline Medium
│                                         │
│  ┌───────────────────────────────────┐ │
│  │ 💵 Saldo do Mês                   │ │ Card 1
│  │ R$ 2.345,80                       │ │ Display Small
│  │ ↑ +12% vs mês anterior            │ │ Body Small + Green
│  └───────────────────────────────────┘ │
│                                         │
│  ┌─────────────┬─────────────────────┐ │
│  │ 📈 Receitas │ 📉 Despesas         │ │ Cards Row
│  │ R$ 8.500    │ R$ 6.154,20         │ │
│  └─────────────┴─────────────────────┘ │
│                                         │
│  📊 Gastos por Categoria (Mês Atual)   │ Title Medium
│  ┌───────────────────────────────────┐ │
│  │ [Gráfico Pizza com cores vibrantes]│ │ 200dp height
│  │                                   │ │
│  │ 🚗 Mobilidade: 35%               │ │
│  │ 🍔 Alimentação: 25%              │ │
│  │ 💪 Saúde: 20%                    │ │
│  │ ❤️ Relacionamento: 12%           │ │
│  │ 🏠 Moradia: 8%                   │ │
│  └───────────────────────────────────┘ │
│                                         │
│  🎯 Metas Ativas                       │ Title Medium
│  ┌───────────────────────────────────┐ │
│  │ 🇨🇭 Fundo Suíça                   │ │ Card com Progress
│  │ CHF 1.967 de CHF 50.000           │ │
│  │ [Progress Bar: 4%]                │ │ LinearProgressIndicator
│  │ 📅 11 meses restantes             │ │
│  │ 💡 Aporte necessário: R$ 26.634/mês│ │
│  └───────────────────────────────────┘ │
│                                         │
│  ⚠️ Alertas Pendentes                  │ Title Medium
│  ┌───────────────────────────────────┐ │
│  │ 🔧 Troca de óleo JAC J2            │ │ Alert Card (Warning)
│  │ Vence em 250 km ou 15 dias        │ │
│  │ [Adiar] [Concluído]               │ │ Action Buttons
│  └───────────────────────────────────┘ │
│                                         │
├─────────────────────────────────────────┤
│ [BottomNavigationBar]                   │ 80dp
└─────────────────────────────────────────┘
│ [FAB: +]                                │ 56dp floating
```

### Componentes Detalhados

#### TopAppBar
```
Height: 56dp
Background: Primary
Title: "Finance App" (Title Large, OnPrimary)
Actions:
  - IconButton (Goals icon: EmojiEvents)
    → Navega para tela de Metas
```

#### Card Saldo do Mês
```
Width: Match Parent
Padding: 16dp
Background: PrimaryContainer
Corner Radius: 16dp
Elevation: 2dp

Content:
  - Label: "💵 Saldo do Mês" (Title Medium, OnPrimaryContainer)
  - Valor: "R$ 2.345,80" (Display Small, Bold, OnPrimaryContainer)
  - Variação: "↑ +12% vs mês anterior" (Body Small, Color: #2E7D32 if positive, #C62828 if negative)
```

#### Cards Receitas/Despesas (Row)
```
Layout: Row com 2 cards de largura igual
Spacing: 8dp entre cards

Card Receitas:
  Background: Color(0xFFE8F5E9) // Verde claro
  Icon: TrendingUp
  Color: #2E7D32
  
Card Despesas:
  Background: Color(0xFFFFEBEE) // Vermelho claro
  Icon: TrendingDown
  Color: #C62828
```

#### Gráfico Pizza
```
Biblioteca: MPAndroidChart (PieChart)
Height: 200dp
Configurações:
  - Hole radius: 50% (donut chart)
  - Rotation enabled: true
  - Legend position: Bottom
  - Value text size: 12sp
  
Cores por categoria:
  Mobilidade: #1B5E20
  Alimentação: #FF6F00
  Saúde: #0277BD
  Relacionamento: #E91E63
  Moradia: #9C27B0
```

#### Card Meta (Fundo Suíça)
```
Background: TertiaryContainer
Icon: 🇨🇭 (emoji ou flag icon)
Progress Bar:
  - Track color: OnTertiaryContainer with 0.3 alpha
  - Indicator color: Tertiary
  - Height: 8dp
  - Corner radius: 4dp
  
Layout:
  Column (16dp padding) {
    Row { Icon + Text("Fundo Suíça") }
    Text("CHF 1.967 de CHF 50.000") // Body Medium
    LinearProgressIndicator(progress = 0.04f)
    Row {
      Icon(DateRange) + Text("11 meses restantes")
    }
    Row {
      Icon(Lightbulb) + Text("Aporte necessário: R$ 26.634/mês")
    }
  }
```

#### Alert Card
```
Background: Color(0xFFFFF3E0) // Laranja muito claro
Border: 1dp solid SecondaryContainer
Icon: Build (wrench)
Color: Secondary

Action Buttons:
  - TextButton("Adiar")
  - FilledTonalButton("Concluído")
```

#### BottomNavigationBar
```
Height: 80dp
Background: Surface
Elevation: 3dp

Items (4):
  1. Dashboard (Home icon, selected)
  2. Transações (List icon)
  3. Cupom (CameraAlt icon)
  4. Assistente (SmartToy icon)

Selected item:
  - Indicator: PrimaryContainer (pill shape)
  - Icon color: OnPrimaryContainer
  - Label color: OnPrimaryContainer
  
Unselected items:
  - Icon color: OnSurfaceVariant
  - Label color: OnSurfaceVariant
```

#### FAB (Floating Action Button)
```
Size: 56dp
Position: Bottom End, 16dp margin
Background: Primary
Icon: Add (OnPrimary)
Elevation: 6dp
OnClick: Abre dialog "Nova Transação"
```

---

## 📱 TELA 2: TRANSAÇÕES (Lista)

### Layout Structure
```
┌─────────────────────────────────────────┐
│ [TopAppBar: Transações]      [Filter]   │
├─────────────────────────────────────────┤
│ [SearchBar: Buscar transações...]       │ 56dp
├─────────────────────────────────────────┤
│ [Chip Filter Row]                       │ 48dp
│ [Todas] [Receitas] [Despesas] [Mês]    │
├─────────────────────────────────────────┤
│                                         │
│  📅 Hoje - 25 Abr 2026                  │ Sticky Header
│  ┌───────────────────────────────────┐ │
│  │ 🚗 Gasolina Shell                 │ │ Transaction Item
│  │ Mobilidade > Combustível          │ │
│  │                      -R$ 287,50   │ │ Amount (Red)
│  │ 14:32                             │ │
│  └───────────────────────────────────┘ │
│  ┌───────────────────────────────────┐ │
│  │ 💪 Academia SmartFit              │ │
│  │ Saúde > Mensalidade               │ │
│  │                       -R$ 89,90   │ │
│  └───────────────────────────────────┘ │
│                                         │
│  📅 Ontem - 24 Abr 2026                 │
│  ┌───────────────────────────────────┐ │
│  │ 💼 Salário                        │ │
│  │ Receitas > Salário                │ │
│  │                      +R$ 5.200,00 │ │ Amount (Green)
│  └───────────────────────────────────┘ │
│  ┌───────────────────────────────────┐ │
│  │ ❤️ Flores para Namorada            │ │
│  │ Relacionamento                    │ │
│  │ #namorada #flores                 │ │ Tags
│  │                       -R$ 120,00  │ │
│  └───────────────────────────────────┘ │
│                                         │
│  📅 23 Abr 2026                         │
│  [...]                                  │
│                                         │
├─────────────────────────────────────────┤
│ [BottomNavigationBar]                   │
└─────────────────────────────────────────┘
│ [FAB: +]                                │
```

### Componentes Detalhados

#### SearchBar
```
Height: 56dp
Margin: 16dp horizontal, 8dp vertical
Background: SurfaceVariant
Corner Radius: 28dp (pill shape)
Leading Icon: Search
Placeholder: "Buscar transações..." (OnSurfaceVariant)
OnClick: Expande para full search mode
```

#### Filter Chips (Horizontal Scroll)
```
Height: 48dp
Padding: 8dp horizontal
Spacing: 8dp entre chips

FilterChip variants:
  - "Todas" (selected by default)
  - "Receitas" (green tint)
  - "Despesas" (red tint)
  - "Este Mês" (date picker trigger)
  
Selected chip:
  Background: PrimaryContainer
  Border: 1dp Primary
  Text: OnPrimaryContainer
  
Unselected:
  Background: Surface
  Border: 1dp Outline
  Text: OnSurface
```

#### Transaction List Item
```
Height: 72dp (min)
Padding: 16dp
Background: Surface
Divider: 1dp OnSurfaceVariant (0.12 alpha)

Layout (Row):
  Leading:
    - Icon Circle (40dp, category color background)
    - Category emoji or icon
    
  Content (Column, weight = 1):
    - Title (Body Large, OnSurface) // "Gasolina Shell"
    - Subtitle (Body Small, OnSurfaceVariant) // "Mobilidade > Combustível"
    - Tags (Label Small, wrapped chips if present) // "#jac_j2"
    
  Trailing (Column, align end):
    - Amount (Title Medium, color based on type)
      Expense: Error color
      Income: #2E7D32 (green)
    - Time (Label Small, OnSurfaceVariant) // "14:32"

OnClick: Navega para TransactionDetailScreen
OnLongPress: Mostra bottom sheet com ações (Editar/Deletar)
```

#### Sticky Date Header
```
Height: 40dp
Padding: 16dp horizontal, 8dp vertical
Background: Background (sticky)
Text: Title Small, OnBackground
Format: "📅 Hoje - 25 Abr 2026" ou "📅 23 Abr 2026"
```

---

## 📱 TELA 3: ESCANEAR CUPOM (OCR)

### Layout Structure
```
┌─────────────────────────────────────────┐
│ [TopAppBar: PriceVision]        [Close] │
├─────────────────────────────────────────┤
│                                         │
│  [Camera Preview - Fullscreen]          │ Match Parent
│                                         │
│  ┌─────────────────────────────────┐   │ Overlay Guide
│  │                                 │   │
│  │   📄 Alinhe o cupom aqui       │   │
│  │                                 │   │
│  └─────────────────────────────────┘   │
│                                         │
│  [Capture Button]                       │ Bottom Center
│                                         │
│  [Gallery Icon]      [Flash Toggle]     │ Bottom Corners
│                                         │
└─────────────────────────────────────────┘
```

### Tela de Processamento (após captura)
```
┌─────────────────────────────────────────┐
│ [TopAppBar: Processando...]             │
├─────────────────────────────────────────┤
│                                         │
│  [Thumbnail do cupom]                   │ 120dp height
│                                         │
│  🤖 Analisando cupom com IA...          │ Body Large
│  [CircularProgressIndicator]            │
│                                         │
│  ✓ Texto extraído                       │ Steps indicator
│  ⏳ Identificando itens...              │
│  ⏳ Calculando total...                 │
│                                         │
└─────────────────────────────────────────┘
```

### Tela de Confirmação (após OCR)
```
┌─────────────────────────────────────────┐
│ [TopAppBar: Confirmar Dados]   [Save]   │
├─────────────────────────────────────────┤
│                                         │
│  📍 Posto Shell Anhanguera              │ Title Medium
│  📅 23/04/2026 - 14:32                  │ Body Medium
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━   │ Divider
│                                         │
│  📝 Itens Extraídos:                    │
│  ┌───────────────────────────────────┐ │
│  │ ☑ Gasolina Comum                  │ │ Checkbox Item
│  │   35.5L × R$ 6.10 = R$ 216.55     │ │
│  │ [Edit] [Delete]                   │ │
│  └───────────────────────────────────┘ │
│  ┌───────────────────────────────────┐ │
│  │ ☐ Limpador Para-brisas            │ │ Unchecked
│  │   1 × R$ 18.90 = R$ 18.90         │ │
│  └───────────────────────────────────┘ │
│  ┌───────────────────────────────────┐ │
│  │ ☑ Óleo de Motor 1L                │ │
│  │   2 × R$ 26.00 = R$ 52.00         │ │
│  └───────────────────────────────────┘ │
│                                         │
│  [+ Adicionar Item Manual]              │ TextButton
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━   │
│                                         │
│  💰 Total:                    R$ 287,45 │ Display Small
│  ⚠️ Confiança: 92%                     │ Label (green if >80%)
│                                         │
│  📂 Categoria                           │
│  [Dropdown: Mobilidade > Combustível]   │ OutlinedTextField
│                                         │
│  🏷️ Tags (opcional)                     │
│  [TextField: #jac_j2, #shell...]        │
│                                         │
│  📝 Observações                         │
│  [TextField multiline]                  │
│                                         │
├─────────────────────────────────────────┤
│ [Cancel Button] [Save Button]           │ Bottom Actions
└─────────────────────────────────────────┘
```

### Componentes Detalhados

#### Camera Preview
```
Full screen camera feed usando CameraX API
Overlay guide:
  - Dashed border box (center, 70% width)
  - Semi-transparent background (0.5 alpha)
  - Icon: ReceiptLong
  - Text: "Alinhe o cupom aqui"
```

#### Capture Button
```
Size: 72dp
Position: Bottom center, 24dp margin bottom
Background: Primary
Icon: Camera (OnPrimary)
Elevation: 6dp
Shape: Circle
OnClick: Captura foto e envia para Vision API
```

#### Extracted Item Card
```
Background: Surface
Border: 1dp Outline
Padding: 12dp
Corner Radius: 8dp

Layout (Row):
  - Checkbox (leading)
  - Column (content):
    - Item name (Body Medium)
    - Calculation (Body Small, OnSurfaceVariant)
  - Row (actions):
    - IconButton(Edit)
    - IconButton(Delete)
    
Selected items (checked) serão salvos
```

#### Confidence Badge
```
Se >= 80%:
  Icon: CheckCircle
  Color: #2E7D32
  Background: Color(0xFFE8F5E9)
  
Se < 80%:
  Icon: Warning
  Color: #FF6F00
  Background: Color(0xFFFFE0B2)
  Text: "Revise os dados antes de salvar"
```

---

## 📱 TELA 4: ASSISTENTE (Text-to-SQL)

### Layout Structure
```
┌─────────────────────────────────────────┐
│ [TopAppBar: Assistente IA]      [Clear] │
├─────────────────────────────────────────┤
│                                         │
│  💡 Sugestões Rápidas:                  │ Title Small
│  [Chip: Quanto gastei com carro?]       │ Suggestion Chips
│  [Chip: Resumo do mês]                  │
│  [Chip: Maiores despesas]               │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━   │
│                                         │
│  [Chat Bubble - User]                   │ Conversation
│  "Quanto gastei com gasolina em abril?" │
│                                         │
│  [Chat Bubble - Assistant]              │
│  🤖 Consultando seus dados...           │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━   │
│  💰 Gastos com Gasolina (Abril 2026)   │
│  ┌───────────────────────────────────┐ │ Result Card
│  │ Total: R$ 1.234,50                │ │
│  │ Média por abastecimento: R$ 287   │ │
│  │ Quantidade: 5 abastecimentos      │ │
│  │                                   │ │
│  │ [Ver Gráfico] [Ver Transações]   │ │ Action Buttons
│  └───────────────────────────────────┘ │
│                                         │
│  [Chat Bubble - User]                   │
│  "E no total do ano?"                   │
│                                         │
│  [...]                                  │
│                                         │
├─────────────────────────────────────────┤
│ [Input Field]               [Send][Mic] │ Bottom Input
└─────────────────────────────────────────┘
```

### Componentes Detalhados

#### Suggestion Chips
```
Horizontal scroll
Height: 32dp
Background: SecondaryContainer
Text: OnSecondaryContainer, Label Medium
Corner Radius: 16dp
Padding: 12dp horizontal

OnClick: Preenche input field e envia query
```

#### Chat Bubbles

**User Bubble:**
```
Alignment: End (right)
Background: PrimaryContainer
Text: OnPrimaryContainer
Max width: 80%
Padding: 12dp
Corner Radius: 16dp (top-left, top-right, bottom-left), 4dp (bottom-right)
Margin: 8dp
```

**Assistant Bubble:**
```
Alignment: Start (left)
Background: SurfaceVariant
Text: OnSurfaceVariant
Max width: 85%
Padding: 12dp
Corner Radius: 16dp (top-left, top-right, bottom-right), 4dp (bottom-left)
Margin: 8dp

Pode conter:
  - Texto simples
  - Result Cards (embedded)
  - Loading indicator (CircularProgress)
```

#### Result Card (dentro do chat)
```
Background: Surface
Elevation: 1dp
Padding: 16dp
Corner Radius: 12dp
Border: 1dp Primary

Content:
  - Título (Title Medium, Primary)
  - Métricas (Body Large, OnSurface)
  - Sub-métricas (Body Small, OnSurfaceVariant)
  - Action buttons (TextButton row)
```

#### Input Bar
```
Height: 56dp
Background: SurfaceVariant
Padding: 8dp

Layout (Row):
  - TextField (weight = 1):
    Placeholder: "Pergunte algo sobre suas finanças..."
    No border (transparent)
    
  - IconButton (Send):
    Icon: Send
    Enabled only if text.isNotEmpty()
    
  - IconButton (Mic):
    Icon: Mic
    OnClick: Abre speech-to-text
    OnLongPress: Continuous recording
```

---

## 📱 TELA 5: METAS (Goals)

### Layout Structure
```
┌─────────────────────────────────────────┐
│ [TopAppBar: Minhas Metas]       [Add]   │
├─────────────────────────────────────────┤
│                                         │
│  🎯 Metas Ativas (2)                    │ Title Medium
│                                         │
│  ┌───────────────────────────────────┐ │
│  │ 🇨🇭 Fundo Suíça                   │ │ Goal Card Expanded
│  │ CHF 1.967 / CHF 50.000            │ │
│  │                                   │ │
│  │ [Progress Bar: 4%]                │ │
│  │                                   │ │
│  │ 📊 Detalhes:                      │ │
│  │ • Meta: CHF 50.000                │ │
│  │ • Prazo: 31/03/2027 (11 meses)    │ │
│  │ • Aporte necessário: R$ 26.634/mês│ │
│  │ • Cotação atual: R$ 6,10/CHF      │ │
│  │                                   │ │
│  │ 📈 Histórico de Aportes:          │ │
│  │ [Mini Line Chart]                 │ │ 100dp height
│  │                                   │ │
│  │ ⚠️ Você está R$ 8.500 abaixo!     │ │ Warning banner
│  │ Sugestão: +R$ 4.250/mês pelos     │ │
│  │ próximos 2 meses                  │ │
│  │                                   │ │
│  │ [Simular] [Editar] [Arquivar]    │ │
│  └───────────────────────────────────┘ │
│                                         │
│  ┌───────────────────────────────────┐ │
│  │ 💼 Evolução Pleno (Set/2026)      │ │ Goal Card Collapsed
│  │ Simular impacto de promoção       │ │
│  │                                   │ │
│  │ [Tap para expandir]               │ │
│  └───────────────────────────────────┘ │
│                                         │
│  🏁 Metas Concluídas (1)                │ Title Small
│  ┌───────────────────────────────────┐ │
│  │ ✅ Fundo Emergência               │ │ Collapsed (gray)
│  │ Concluída em 15/01/2026           │ │
│  └───────────────────────────────────┘ │
│                                         │
└─────────────────────────────────────────┘
```

### Componentes Detalhados

#### Goal Card (Expanded)
```
Background: gradient(TertiaryContainer to Surface)
Elevation: 2dp
Padding: 20dp
Corner Radius: 16dp
Margin: 8dp vertical

Header:
  - Icon + Title (Headline Small)
  - Amount progress (Display Small, Bold)
  
Progress Bar:
  Height: 12dp
  Corner Radius: 6dp
  Track: OnTertiaryContainer (0.3 alpha)
  Indicator: Tertiary
  Animated on appear
  
Details Section:
  Icon bullets (Label Large)
  
Chart:
  Library: MPAndroidChart LineChart
  Height: 100dp
  Data: Últimos 6 meses de aportes
  
Warning Banner (if applicable):
  Background: Color(0xFFFFF3E0)
  Icon: Warning (Secondary)
  Text: Body Medium
  
Actions:
  Row of TextButtons
  Spacing: 8dp
```

#### Goal Card (Collapsed)
```
Height: 72dp
Background: Surface
Elevation: 1dp
OnClick: Expande card
```

#### Add Goal Dialog (Modal)
```
Width: 90% screen width
Max width: 400dp
Corner Radius: 28dp

Fields:
  - TextField: Nome da meta
  - TextField: Valor alvo (number keyboard)
  - Dropdown: Moeda (BRL, CHF, USD, EUR)
  - DatePicker: Data alvo
  - TextField: Aporte inicial (opcional)
  
Buttons:
  - TextButton("Cancelar")
  - FilledButton("Criar Meta")
```

---

## 📱 TELA 6: ADICIONAR TRANSAÇÃO (Dialog/Bottom Sheet)

### Layout (Bottom Sheet)
```
┌─────────────────────────────────────────┐
│ [Drag Handle]                           │ 4dp height
├─────────────────────────────────────────┤
│                                         │
│  💰 Nova Transação                      │ Headline Small
│                                         │
│  [Segmented Button: Despesa | Receita]  │ Toggle
│                                         │
│  💵 Valor                                │
│  [TextField: R$ 0,00]                   │ Large, number keyboard
│                                         │
│  📅 Data                                 │
│  [DatePicker Field: Hoje]               │
│                                         │
│  📂 Categoria                            │
│  [Dropdown: Selecione...]               │
│   > Mobilidade                          │
│   > Saúde                               │
│   > Alimentação                         │
│   > ...                                 │
│                                         │
│  🏷️ Subcategoria                        │
│  [Dropdown: Automático após categoria]  │
│                                         │
│  📝 Descrição (opcional)                │
│  [TextField multiline]                  │
│                                         │
│  🏷 Tags (opcional)                      │
│  [TextField: #tag1, #tag2...]           │
│                                         │
│  📸 Anexar Cupom                         │
│  [Button: Escolher arquivo]             │
│                                         │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━   │
│                                         │
│  [Cancelar]              [Salvar]       │ Action Row
│                                         │
└─────────────────────────────────────────┘
```

### Componentes Detalhados

#### Segmented Button (Type Toggle)
```
Width: Match Parent
Height: 48dp

Despesa (default selected):
  Background: Color(0xFFFFEBEE)
  Text: Error
  Icon: TrendingDown
  
Receita:
  Background: Color(0xFFE8F5E9)
  Text: #2E7D32
  Icon: TrendingUp
```

#### Value TextField
```
Height: 64dp
Text Size: Display Small
Prefix: "R$"
Keyboard: Decimal number
Format: Auto-format com vírgula (1234.56 → 1.234,56)
Focus: Auto-focus on open
```

#### Category Dropdown
```
Exposed Dropdown Menu
Items grouped by icon:
  🚗 Mobilidade
  💪 Saúde
  🍔 Alimentação
  ❤️ Relacionamento
  🏠 Moradia
  
On selection: Carrega subcategorias correspondentes
```

#### Tags TextField
```
Chip input field
Auto-complete de tags usadas recentemente
Formato: # é adicionado automaticamente
Visual: Cada tag vira um chip dentro do field
```

---

## 🎨 COMPONENTES REUTILIZÁVEIS

### 1. MoneyText Component
```kotlin
@Composable
fun MoneyText(
    amount: Double,
    isIncome: Boolean,
    style: TextStyle = MaterialTheme.typography.titleMedium
) {
    val color = if (isIncome) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
    val prefix = if (isIncome) "+" else "-"
    
    Text(
        text = "${prefix}R$ ${amount.formatMoney()}",
        style = style,
        color = color,
        fontWeight = FontWeight.Bold
    )
}
```

### 2. CategoryIcon Component
```kotlin
@Composable
fun CategoryIcon(
    category: Category,
    size: Dp = 40.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = Color(category.color).copy(alpha = 0.2f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.emoji,
            fontSize = (size.value * 0.5).sp
        )
    }
}
```

### 3. ProgressCard Component
```kotlin
@Composable
fun ProgressCard(
    title: String,
    current: Double,
    target: Double,
    currency: String = "BRL",
    color: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(padding = 16.dp) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(height = 8.dp)
            
            Row {
                Text("${currency} ${current.format()}")
                Text(" de ")
                Text("${currency} ${target.format()}", fontWeight = Bold)
            }
            
            LinearProgressIndicator(
                progress = (current / target).toFloat(),
                color = color,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

---

## 📐 RESPONSIVIDADE

### Breakpoints
```
Compact: < 600dp width (phones portrait)
Medium: 600-840dp (phones landscape, small tablets)
Expanded: > 840dp (tablets, foldables)
```

### Adaptive Layout Rules
```
Compact (default):
  - Navigation: BottomNavigationBar
  - Content: Single column
  - Dialogs: Full screen
  
Medium:
  - Navigation: NavigationRail (lateral)
  - Content: Two columns onde aplicável
  - Dialogs: Centered, max 600dp width
  
Expanded:
  - Navigation: Permanent NavigationDrawer
  - Content: Master-Detail (lista + detalhe lado a lado)
  - Dialogs: Centered, max 800dp width
```

---

## ♿ ACESSIBILIDADE

### Checklist
- [ ] Todos os ícones têm contentDescription
- [ ] Contraste mínimo 4.5:1 (texto normal)
- [ ] Contraste mínimo 3:1 (texto grande)
- [ ] Touch targets mínimo 48dp
- [ ] Suporte a TalkBack testado
- [ ] Suporte a fontes grandes (até 200% scale)
- [ ] Foco de teclado visível
- [ ] Sem dependência apenas de cor para informação

### Semantic Properties
```kotlin
// Exemplo de botão acessível
IconButton(
    onClick = { /* ... */ },
    modifier = Modifier.semantics {
        contentDescription = "Adicionar nova transação"
        role = Role.Button
    }
) {
    Icon(Icons.Default.Add, contentDescription = null) // Null pois já está no parent
}
```

---

## 🌙 DARK MODE

### Color Mapping
```
Light → Dark
Background (#FAFAFA) → Surface (#121212)
Surface (#FFFFFF) → Surface (#1E1E1E)
Primary (#1B5E20) → Primary (#81C784)
OnPrimary (#FFFFFF) → OnPrimary (#003300)

Elevations em dark mode:
  Use color overlays ao invés de shadows
  Level 1: +5% white overlay
  Level 2: +7% white overlay
  Level 3: +8% white overlay
```

### Images & Icons
```
Assets que precisam variante dark:
  - Logo (se houver)
  - Ilustrações de empty states
  - Charts background

Usar:
  - res/drawable-night/ para variantes
  - ou Tinted vectors com OnSurface color
```

---

## 📦 ASSETS NECESSÁRIOS

### Icons (Material Icons Extended)
```
Dashboard, List, CameraAlt, SmartToy, Add, TrendingUp, TrendingDown,
EmojiEvents, DateRange, FilterList, Search, Edit, Delete, MoreVert,
Warning, CheckCircle, Lightbulb, Build, AttachMoney, Category,
LocalOffer, Description, Send, Mic, Close, ArrowBack, Settings
```

### Custom Icons (criar SVG)
```
- Categoria Mobilidade (car)
- Categoria Saúde (dumbbell)
- Categoria Relacionamento (heart)
- Empty state illustrations (opcional)
```

### Lottie Animations (opcional)
```
- Loading animation (processando cupom)
- Success animation (meta alcançada)
- Empty state animations
```

---

## 🎬 ANIMAÇÕES & MICRO-INTERAÇÕES

### Transitions
```
Screen transitions: 300ms slide + fade
Bottom Sheet: 250ms slide up with overshoot
FAB: 200ms scale on press
Cards: 150ms elevation change on press
```

### Loading States
```
Skeleton screens para listas (shimmer effect)
CircularProgressIndicator para API calls
Linear progress para uploads/processamento
```

### Gestures
```
Swipe to dismiss: Transaction items (reveal delete)
Pull to refresh: Transaction list
Long press: Contextual menu
Drag to reorder: Categories (futuro)
```

---

## ✅ DESIGN CHECKLIST FINAL

- [ ] Todas as 5 telas principais especificadas
- [ ] Componentes reutilizáveis documentados
- [ ] Paleta de cores completa (light + dark)
- [ ] Tipografia com escalas definidas
- [ ] Espaçamentos padronizados
- [ ] States definidos (loading, error, empty, success)
- [ ] Acessibilidade considerada
- [ ] Responsividade planejada
- [ ] Animações documentadas
- [ ] Assets listados

---

**Próximo passo:** Exportar especificações para Figma/Stitch e gerar screens Android Compose.
