# Web Scraper API - Java 17 + Playwright

Uma API REST completa para captura de screenshots e raspagem de dados de sites usando Java 17, Spring Boot e Microsoft Playwright.

## 🚀 Funcionalidades

### Screenshots
- ✅ Captura de screenshot simples
- ✅ Captura de página inteira (full page)
- ✅ Captura em Base64
- ✅ Captura de elementos específicos
- ✅ Capturas personalizadas com opções avançadas

### Web Scraping
- ✅ Extração de texto de elementos
- ✅ Extração de múltiplos textos
- ✅ Extração de atributos de elementos
- ✅ Extração completa de informações da página
- ✅ Execução de JavaScript personalizado

## 🛠️ Tecnologias

- **Java 17** - Versão LTS do Java
- **Spring Boot 3.2.1** - Framework web
- **Microsoft Playwright 1.40.0** - Automação web
- **Maven** - Gerenciamento de dependências

## 📦 Instalação e Execução

### Pré-requisitos
- Java 17 instalado
- Maven 3.6+ instalado

### Passos para executar

1. **Clone ou baixe o projeto**

2. **Instale as dependências do Playwright**
   ```bash
   mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
   ```

3. **Compile o projeto**
   ```bash
   mvn clean compile
   ```

4. **Execute a aplicação**
   ```bash
   mvn spring-boot:run
   ```

A aplicação estará disponível em: `http://localhost:8080`

## 📚 Endpoints da API

### Screenshots

#### 1. Captura Simples
```http
POST /api/screenshot/capture
Content-Type: application/json

{
  "url": "https://example.com",
  "fileName": "screenshot.png"
}
```

#### 2. Página Inteira
```http
POST /api/screenshot/capture/fullpage
Content-Type: application/json

{
  "url": "https://example.com",
  "fileName": "fullpage.png"
}
```

#### 3. Base64
```http
POST /api/screenshot/capture/base64
Content-Type: application/json

{
  "url": "https://example.com"
}
```

#### 4. Elemento Específico
```http
POST /api/screenshot/capture/element
Content-Type: application/json

{
  "url": "https://example.com",
  "selector": ".header",
  "fileName": "element.png"
}
```

#### 5. Captura Personalizada
```http
POST /api/screenshot/capture/custom
Content-Type: application/json

{
  "url": "https://example.com",
  "fileName": "custom.png",
  "fullPage": true,
  "format": "jpeg",
  "quality": 80,
  "width": 1920,
  "height": 1080
}
```

#### 6. Teste Rápido (GET)
```http
GET /api/screenshot/test?url=https://example.com&fileName=test.png
```

### Web Scraping

#### 1. Extrair Texto
```http
POST /api/scraping/text
Content-Type: application/json

{
  "url": "https://example.com",
  "selector": "h1"
}
```

#### 2. Múltiplos Textos
```http
POST /api/scraping/texts
Content-Type: application/json

{
  "url": "https://example.com",
  "selector": "p"
}
```

#### 3. Atributos
```http
POST /api/scraping/attributes
Content-Type: application/json

{
  "url": "https://example.com",
  "selector": "a",
  "attributes": ["href", "title", "class"]
}
```

#### 4. Informações da Página
```http
POST /api/scraping/page-info
Content-Type: application/json

{
  "url": "https://example.com"
}
```

#### 5. JavaScript Personalizado
```http
POST /api/scraping/execute-script
Content-Type: application/json

{
  "url": "https://example.com",
  "script": "document.title"
}
```

#### 6. Teste Rápido (GET)
```http
GET /api/scraping/test?url=https://example.com&selector=title
```

## 🧪 Testando com o Link Fornecido

Para testar com o link do ChatGPT fornecido:

### Screenshot
```bash
curl -X POST http://localhost:8080/api/screenshot/capture \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://chatgpt.com/c/68c98b88-8930-8330-a588-171cea0ad108",
    "fileName": "chatgpt_screenshot.png"
  }'
```

### Raspagem
```bash
curl -X POST http://localhost:8080/api/scraping/page-info \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://chatgpt.com/c/68c98b88-8930-8330-a588-171cea0ad108"
  }'
```

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/screenshot/
│   │   ├── WebScraperApplication.java      # Classe principal
│   │   ├── controller/
│   │   │   ├── ScreenshotController.java   # Endpoints de screenshot
│   │   │   └── WebScrapingController.java  # Endpoints de scraping
│   │   ├── service/
│   │   │   ├── ScreenshotService.java      # Lógica de screenshots
│   │   │   └── WebScrapingService.java     # Lógica de scraping
│   │   ├── dto/
│   │   │   ├── ScreenshotRequest.java      # DTO para requests
│   │   │   ├── ScrapingRequest.java        # DTO para scraping
│   │   │   └── ApiResponse.java            # DTO para responses
│   │   └── config/
│   │       └── GlobalExceptionHandler.java # Tratamento de erros
│   └── resources/
│       └── application.yml                 # Configurações
└── screenshots/                            # Diretório de saída
```

## ⚙️ Configurações

As configurações podem ser alteradas no arquivo `application.yml`:

```yaml
server:
  port: 8080  # Porta da aplicação

app:
  screenshot:
    default-timeout: 30000      # Timeout padrão (ms)
    default-format: png         # Formato padrão
    output-directory: screenshots/
  scraping:
    default-timeout: 30000      # Timeout padrão (ms)
    max-wait-time: 10000       # Tempo máximo de espera (ms)
```

## 🔧 Exemplos de Uso

### Exemplo completo com cURL

```bash
# Screenshot simples
curl -X POST http://localhost:8080/api/screenshot/capture \
  -H "Content-Type: application/json" \
  -d '{"url": "https://github.com", "fileName": "github.png"}'

# Extrair título da página
curl -X POST http://localhost:8080/api/scraping/text \
  -H "Content-Type: application/json" \
  -d '{"url": "https://github.com", "selector": "title"}'

# Informações completas da página
curl -X POST http://localhost:8080/api/scraping/page-info \
  -H "Content-Type: application/json" \
  -d '{"url": "https://github.com"}'
```

## 🚨 Tratamento de Erros

A API retorna respostas padronizadas:

```json
{
  "success": true,
  "message": "Operação realizada com sucesso",
  "data": { ... },
  "timestamp": "2024-01-15T10:30:00"
}
```

Em caso de erro:

```json
{
  "success": false,
  "message": "Erro ao capturar screenshot: Timeout",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

## 📝 Logs

Os logs são salvos em:
- Console: Formato simples
- Arquivo: `logs/web-scraper-api.log`

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

---

**Desenvolvido com ❤️ usando Java 17 e Playwright**