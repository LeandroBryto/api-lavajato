# API de Raspagem - Sistema de Extração de Editais PNCP

Esta API permite extrair informações de editais do Portal Nacional de Contratações Públicas (PNCP) usando web scraping com Playwright.

## Novos Endpoints PNCP

### 1. Buscar Editais Completos
```
GET /api/pncp/editais
```

**Parâmetros opcionais:**
- `url`: URL específica da página de editais (padrão: página principal do PNCP)
- `busca`: Termo de busca para filtrar editais
- `ufs`: Lista de UFs separadas por vírgula (ex: "DF,SP,RJ")
- `pagina`: Número da página (padrão: 1)
- `tamanho`: Tamanho da página (padrão: 20)

**Exemplo de resposta:**
```json
{
  "success": true,
  "message": "Encontrados 15 editais com informações completas",
  "data": [
    {
      "numeroEdital": "90075/2025",
      "idContratacao": "03288908000483-1-000068/2025",
      "modalidade": "Pregão - Eletrônico",
      "ultimaAtualizacao": "19/09/2025",
      "orgao": "SESC-SERVICO SOCIAL DO COMERCIO-ADMINISTRACAO REGIONAL DO DF",
      "local": "Brasília/DF",
      "objeto": "Contratação de empresa especializada na organização e coordenação de eventos...",
      "valido": true
    }
  ]
}
```

### 2. Extrair por Seletor Customizado
```
GET /api/pncp/extrair
```

**Parâmetros obrigatórios:**
- `url`: URL da página para extrair informações
- `seletor`: Seletor CSS para localizar os elementos

**Exemplo:**
```
GET /api/pncp/extrair?url=https://pncp.gov.br/app/editais&seletor=strong:has-text('Edital nº')
```

### 3. Buscar com URL Completa
```
POST /api/pncp/buscar
```

**Body:**
```json
{
  "url": "https://pncp.gov.br/app/editais?q=serviços&uf=DF"
}
```

## Seletores Específicos para PNCP

Com base na estrutura HTML da página de editais, os seguintes seletores são utilizados:

### Seletores Principais:
- **Itens de edital**: `a.br-item`
- **Número do edital**: `div.mb-1 strong:has-text('Edital nº')`
- **ID de contratação**: `div.mb-1 span:has-text('Id contratação PNCP:')`
- **Modalidade**: `div.col-auto.mb-1:has-text('Modalidade da Contratação:')`
- **Última atualização**: `div.col-auto.mb-1:has-text('Última Atualização:')`
- **Órgão**: `div.col-auto.mb-1 span:has-text('Órgão:')`
- **Local**: `div.col-auto.mb-1 span:has-text('Local:')`
- **Objeto**: `span:has-text('Objeto:')`

### Exemplos de Uso dos Seletores:

1. **Extrair todos os números de editais:**
   ```
   GET /api/pncp/extrair?url=https://pncp.gov.br/app/editais&seletor=div.mb-1 strong:has-text('Edital nº')
   ```

2. **Extrair todas as modalidades:**
   ```
   GET /api/pncp/extrair?url=https://pncp.gov.br/app/editais&seletor=div.col-auto.mb-1:has-text('Modalidade da Contratação:')
   ```

3. **Extrair todos os órgãos:**
   ```
   GET /api/pncp/extrair?url=https://pncp.gov.br/app/editais&seletor=div.col-auto.mb-1 span:has-text('Órgão:')
   ```

### Mapeamento Campo → Seletor:

| Campo | Elemento HTML | Seletor CSS |
|-------|---------------|-------------|
| `numeroEdital` | `<strong>Edital nº ...</strong>` | `div.mb-1 strong:has-text('Edital nº')` |
| `idContratacao` | `<span><strong>Id contratação PNCP:</strong> ...</span>` | `div.mb-1 span:has-text('Id contratação PNCP:')` |
| `modalidade` | `<div><strong>Modalidade da Contratação:</strong> ...</div>` | `div.col-auto.mb-1:has-text('Modalidade da Contratação:')` |
| `ultimaAtualizacao` | `<div><strong>Última Atualização:</strong> ...</div>` | `div.col-auto.mb-1:has-text('Última Atualização:')` |
| `orgao` | `<span><strong>Órgão:</strong> ...</span>` | `div.col-auto.mb-1 span:has-text('Órgão:')` |
| `local` | `<span><strong>Local:</strong> ...</span>` | `div.col-auto.mb-1 span:has-text('Local:')` |
| `objeto` | `<span><strong>Objeto:</strong> ...</span>` | `span:has-text('Objeto:')` |

## Endpoints Existentes

### Screenshot
```
POST /api/screenshot
```

### Extração Genérica
```
POST /api/extract
```

## Como Executar

### Pré-requisitos
- Java 21 ou superior
- Maven (ou usar o wrapper incluído no projeto)

### Executar a aplicação

1. **Compilar o projeto:**
   ```bash
   mvn clean compile
   ```

2. **Executar a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

3. **Acessar a API:**
   - URL base: `http://localhost:8080`
   - Documentação: `http://localhost:8080/swagger-ui.html` (se configurado)

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/screenshot/
│   │       ├── controller/
│   │       │   └── PncpController.java      # Endpoints PNCP
│   │       ├── dto/
│   │       │   └── PncpEditalResponse.java  # DTO para editais
│   │       └── service/
│   │           └── PncpScrapingService.java # Lógica de extração
│   └── resources/
└── test/
```

## Melhorias Implementadas

### Parsing Mais Preciso
- Seletores específicos baseados na estrutura HTML real da página
- Extração individual de cada campo usando localizadores precisos
- Tratamento de erros por elemento para maior robustez

### Tratamento de Erros
- Continuidade na extração mesmo se alguns elementos falharem
- Logs detalhados para debugging
- Valores padrão "N/A" para campos não encontrados

### Performance
- Reutilização da sessão do navegador para múltiplas extrações
- Timeout configurável para páginas lentas
- Processamento otimizado de elementos

## Notas Técnicas

- A API usa Playwright para automação do navegador
- Modo headless por padrão para melhor performance
- Timeout padrão de 30 segundos para carregamento de páginas
- Suporte a páginas com conteúdo dinâmico (JavaScript)