# 🧪 Guia Completo de Testes - Web Scraper API

Este guia fornece instruções detalhadas sobre como testar todas as funcionalidades da API de captura de screenshots e raspagem de dados.

## 📋 Índice

1. [Preparação do Ambiente](#preparação-do-ambiente)
2. [Executando a Aplicação](#executando-a-aplicação)
3. [Testando com cURL](#testando-com-curl)
4. [Testando com Postman](#testando-com-postman)
5. [Testes Específicos](#testes-específicos)
6. [Troubleshooting](#troubleshooting)

---

## 🔧 Preparação do Ambiente

### Pré-requisitos
- ✅ Java 17 instalado
- ✅ Maven 3.6+ instalado
- ✅ cURL instalado (para testes via linha de comando)
- ✅ Postman instalado (opcional, para interface gráfica)

### Verificando Instalações

```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar cURL
curl --version
```

---

## 🚀 Executando a Aplicação

### Passo 1: Instalar Dependências do Playwright

```bash
# Navegar para o diretório do projeto
cd c:\Users\nokia\Downloads\c09b5606-0097-4509-b051-e0f42b365825\ex

# Instalar browsers do Playwright
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### Passo 2: Compilar o Projeto

```bash
# Limpar e compilar
mvn clean compile

# Ou compilar e executar testes
mvn clean test
```

### Passo 3: Executar a Aplicação

```bash
# Executar com Maven
mvn spring-boot:run
```

**✅ Aplicação rodando em:** `http://localhost:8080`

### Verificando se a Aplicação Está Funcionando

```bash
# Teste simples de conectividade
curl http://localhost:8080/api/screenshot/test?url=https://www.google.com
```

---

## 🌐 Testando com cURL

### 📸 Endpoints de Screenshot

#### 1. Screenshot Simples

```bash
curl -X POST http://localhost:8080/api/screenshot/capture \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.google.com",
    "fileName": "google_screenshot.png"
  }'
```

**Resposta esperada:**
```json
{
  "success": true,
  "message": "Screenshot capturada com sucesso",
  "data": {
    "filePath": "C:\\caminho\\para\\google_screenshot.png",
    "url": "https://www.google.com"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

#### 2. Screenshot de Página Inteira

```bash
curl -X POST http://localhost:8080/api/screenshot/capture/fullpage \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://github.com",
    "fileName": "github_fullpage.png"
  }'
```

#### 3. Screenshot em Base64

```bash
curl -X POST http://localhost:8080/api/screenshot/capture/base64 \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.wikipedia.org"
  }'
```

#### 4. Screenshot de Elemento Específico

```bash
curl -X POST http://localhost:8080/api/screenshot/capture/element \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.google.com",
    "selector": "input[name=\"q\"]",
    "fileName": "google_search_box.png"
  }'
```

#### 5. Screenshot Personalizada

```bash
curl -X POST http://localhost:8080/api/screenshot/capture/custom \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.youtube.com",
    "fileName": "youtube_custom.jpg",
    "fullPage": false,
    "format": "jpeg",
    "quality": 85,
    "width": 1920,
    "height": 1080
  }'
```

#### 6. Teste Rápido (GET)

```bash
curl "http://localhost:8080/api/screenshot/test?url=https://www.stackoverflow.com&fileName=stackoverflow.png"
```

### 🕷️ Endpoints de Web Scraping

#### 1. Extrair Texto de Elemento

```bash
curl -X POST http://localhost:8080/api/scraping/text \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.google.com",
    "selector": "title"
  }'
```

#### 2. Extrair Múltiplos Textos

```bash
curl -X POST http://localhost:8080/api/scraping/texts \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://news.ycombinator.com",
    "selector": ".titleline > a"
  }'
```

#### 3. Extrair Atributos

```bash
curl -X POST http://localhost:8080/api/scraping/attributes \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.github.com",
    "selector": "a.HeaderMenu-link",
    "attributes": ["href", "title", "class"]
  }'
```

#### 4. Informações Completas da Página

```bash
curl -X POST http://localhost:8080/api/scraping/page-info \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.reddit.com"
  }'
```

#### 5. Executar JavaScript

```bash
curl -X POST http://localhost:8080/api/scraping/execute-script \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.google.com",
    "script": "document.querySelectorAll(\"a\").length"
  }'
```

#### 6. Teste Rápido de Scraping (GET)

```bash
curl "http://localhost:8080/api/scraping/test?url=https://www.google.com&selector=title"
```

---

## 📮 Testando com Postman

### Configuração Inicial

1. **Abrir Postman**
2. **Criar Nova Collection:** "Web Scraper API Tests"
3. **Configurar Base URL:** `http://localhost:8080`

### Exemplos de Requests no Postman

#### Screenshot Request
```
Method: POST
URL: {{baseUrl}}/api/screenshot/capture
Headers: 
  Content-Type: application/json
Body (raw JSON):
{
  "url": "https://www.example.com",
  "fileName": "example.png"
}
```

#### Scraping Request
```
Method: POST
URL: {{baseUrl}}/api/scraping/text
Headers: 
  Content-Type: application/json
Body (raw JSON):
{
  "url": "https://www.example.com",
  "selector": "h1"
}
```

### Collection do Postman (Importar)

```json
{
  "info": {
    "name": "Web Scraper API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    }
  ],
  "item": [
    {
      "name": "Screenshot Simples",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"url\": \"https://www.google.com\",\n  \"fileName\": \"google.png\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/screenshot/capture",
          "host": ["{{baseUrl}}"],
          "path": ["api", "screenshot", "capture"]
        }
      }
    }
  ]
}
```

---

## 🎯 Testes Específicos

### Testando com o Link do ChatGPT

```bash
# Screenshot do ChatGPT
curl -X POST http://localhost:8080/api/screenshot/capture \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://chatgpt.com/c/68c98b88-8930-8330-a588-171cea0ad108",
    "fileName": "chatgpt_conversation.png"
  }'

# Informações da página do ChatGPT
curl -X POST http://localhost:8080/api/scraping/page-info \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://chatgpt.com/c/68c98b88-8930-8330-a588-171cea0ad108"
  }'

# Extrair título da conversa
curl -X POST http://localhost:8080/api/scraping/text \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://chatgpt.com/c/68c98b88-8930-8330-a588-171cea0ad108",
    "selector": "title"
  }'
```

### Testes de Validação

#### Teste com URL Inválida
```bash
curl -X POST http://localhost:8080/api/screenshot/capture \
  -H "Content-Type: application/json" \
  -d '{
    "url": "invalid-url",
    "fileName": "test.png"
  }'
```

#### Teste sem URL (Erro de Validação)
```bash
curl -X POST http://localhost:8080/api/screenshot/capture \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "test.png"
  }'
```

#### Teste com Seletor Inexistente
```bash
curl -X POST http://localhost:8080/api/scraping/text \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://www.google.com",
    "selector": ".elemento-que-nao-existe"
  }'
```

### Testes de Performance

#### Múltiplas Requisições Simultâneas
```bash
# Executar em paralelo (Linux/Mac)
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/screenshot/capture \
    -H "Content-Type: application/json" \
    -d "{\"url\": \"https://httpbin.org/delay/2\", \"fileName\": \"test_$i.png\"}" &
done
wait
```

#### Teste de Timeout
```bash
curl -X POST http://localhost:8080/api/screenshot/capture \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://httpbin.org/delay/35",
    "fileName": "timeout_test.png"
  }'
```

---

## 🔍 Troubleshooting

### Problemas Comuns e Soluções

#### 1. Erro: "Connection refused"
**Problema:** A aplicação não está rodando
**Solução:**
```bash
# Verificar se a aplicação está rodando
netstat -an | findstr :8080

# Reiniciar a aplicação
mvn spring-boot:run
```

#### 2. Erro: "Playwright not installed"
**Problema:** Browsers do Playwright não instalados
**Solução:**
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

#### 3. Erro: "Java version incompatible"
**Problema:** Versão do Java incorreta
**Solução:**
```bash
# Verificar versão do Java
java -version

# Deve ser Java 17 ou superior
```

#### 4. Erro: "Screenshot timeout"
**Problema:** Site demora muito para carregar
**Solução:** Usar sites mais rápidos para teste ou aumentar timeout

#### 5. Erro: "Element not found"
**Problema:** Seletor CSS não encontra elemento
**Solução:** Verificar o seletor usando DevTools do browser

### Logs de Debug

```bash
# Verificar logs da aplicação
tail -f logs/web-scraper-api.log

# Ou verificar no console onde a aplicação está rodando
```

### Testando Conectividade

```bash
# Teste básico de conectividade
curl -I http://localhost:8080

# Teste de health check (se disponível)
curl http://localhost:8080/actuator/health
```

---

## 📊 Exemplos de Respostas

### Resposta de Sucesso
```json
{
  "success": true,
  "message": "Screenshot capturada com sucesso",
  "data": {
    "filePath": "C:\\Users\\nokia\\Downloads\\screenshot.png",
    "url": "https://www.google.com"
  },
  "timestamp": "2024-01-15T10:30:00.123"
}
```

### Resposta de Erro
```json
{
  "success": false,
  "message": "Erro ao capturar screenshot: Timeout",
  "data": null,
  "timestamp": "2024-01-15T10:30:00.123"
}
```

### Resposta de Validação
```json
{
  "success": false,
  "message": "Erro de validação: {url=URL é obrigatória}",
  "data": null,
  "timestamp": "2024-01-15T10:30:00.123"
}
```

---

## 🎉 Conclusão

Este guia fornece todos os comandos e exemplos necessários para testar completamente a API Web Scraper. 

**Próximos passos:**
1. Execute os testes básicos primeiro
2. Teste com diferentes sites
3. Experimente diferentes seletores CSS
4. Monitore os logs para debug
5. Use Postman para testes mais complexos

**Dicas importantes:**
- Sempre verifique se a aplicação está rodando antes de testar
- Use sites públicos e acessíveis para testes
- Monitore o diretório de screenshots para ver os resultados
- Consulte os logs em caso de problemas

---

**📞 Suporte:** Consulte os logs da aplicação ou verifique a documentação do Playwright para problemas específicos.