# AGENTS.md — Regras para Codex no projeto ToolHub Backend

Este arquivo define o contexto, as regras, os limites e os padrões obrigatórios para qualquer agente de IA, Codex ou assistente automatizado que trabalhe neste repositório.

O objetivo é garantir que alterações no projeto sejam seguras, consistentes com a arquitetura documentada, fáceis de revisar e alinhadas aos requisitos do sistema ToolHub.

---

## 1. Contexto do projeto

O projeto é o backend do ToolHub — Gerenciador de Ferramentas, um sistema para controle automatizado de ferramentas industriais.

O sistema integra:

- Backend Java / Spring Boot
- API REST
- Autenticação e autorização via JWT
- Banco de dados MySQL
- Comunicação MQTT com dispositivos IoT
- ESP32
- Leitor QR Code via UART
- Trava solenoide
- LED RGB
- Aplicações consumidoras Web e Mobile Android

O backend centraliza as regras de negócio, valida permissões, controla usuários, ferramentas, empréstimos, devoluções, ocorrências, auditoria e comunicação com hardware.

---

## 2. Objetivo do backend

O backend deve:

1. Expor endpoints REST para Web e Mobile.
2. Validar autenticação e autorização via JWT.
3. Persistir dados no MySQL com integridade referencial.
4. Controlar fluxo de empréstimo, devolução e status das ferramentas.
5. Comunicar-se com o Broker MQTT, sem comunicação direta com o ESP32.
6. Publicar comandos para hardware, como abertura de gaveta, acionamento de solenoide e LED RGB.
7. Consumir mensagens vindas do ESP32, como leituras de QR Code.
8. Registrar logs e trilhas de auditoria.
9. Manter documentação da API via Swagger/OpenAPI.
10. Seguir arquitetura em camadas.

---

## 3. Fonte de verdade do projeto

Antes de implementar qualquer alteração relevante, o agente deve considerar como fonte de verdade, nesta ordem:

1. Código existente no repositório.
2. Documentação oficial no Confluence do projeto.
3. Issues, histórias e tarefas do Jira relacionadas.
4. README e arquivos de configuração do projeto.
5. Convenções já utilizadas no código.
6. Boas práticas de Java, Spring Boot, segurança, REST e banco de dados.

Nunca inventar regras de negócio quando houver documentação disponível.

Quando a documentação e o código divergirem, o agente deve:

- Não sobrescrever silenciosamente o comportamento existente.
- Explicar a divergência.
- Propor a correção mais segura.
- Preservar compatibilidade quando possível.
- Solicitar decisão humana quando a alteração afetar regra de negócio crítica.

---

## 4. Arquitetura obrigatória

O projeto deve seguir Arquitetura em Camadas.

As responsabilidades devem ser mantidas separadas:

### 4.1 Controller

Responsável por:

- Expor endpoints REST.
- Receber requisições HTTP.
- Validar entrada básica com annotations, quando aplicável.
- Delegar regras de negócio para Services.
- Retornar respostas adequadas ao cliente.

Controllers não devem conter:

- Regra de negócio complexa.
- Acesso direto ao banco.
- Lógica MQTT.
- Manipulação manual de entidades além do necessário.
- Código duplicado de validação.

### 4.2 Service

Responsável por:

- Concentrar regras de negócio.
- Validar fluxos de empréstimo, devolução, usuário, ferramenta e permissões.
- Orquestrar repositories, mappers, integrações MQTT e logs.
- Controlar transações quando necessário.
- Garantir consistência do domínio.

Services não devem:

- Expor detalhes HTTP.
- Retornar ResponseEntity sem necessidade.
- Conhecer detalhes de Controller.
- Conter SQL manual se já existir Repository adequado.

### 4.3 Repository

Responsável por:

- Comunicação com MySQL via Spring Data JPA.
- Consultas derivadas ou queries específicas.
- Persistência de entidades.

Repositories não devem:

- Ter regra de negócio.
- Validar autorização.
- Montar DTOs complexos sem necessidade.
- Fazer chamadas HTTP ou MQTT.

### 4.4 Model / Entity

Responsável por:

- Representar tabelas e objetos persistidos.
- Declarar relacionamentos JPA.
- Definir constraints e mapeamentos.

Entities não devem:

- Ser expostas diretamente em endpoints públicos quando houver DTO.
- Receber dados brutos de request sem validação.
- Conter lógica de infraestrutura.

### 4.5 DTO

Responsável por:

- Definir payloads de entrada e saída da API.
- Evitar exposição direta de entidades.
- Controlar os dados trafegados entre backend, Web e Mobile.

DTOs devem usar:

- Campos em camelCase.
- Termos em português, mantendo o padrão do projeto.
- Datas em ISO 8601 quando aplicável.
- IDs como inteiros, longs ou UUIDs conforme padrão já existente no domínio.

---

## 5. Padrões de API REST

Todos os endpoints devem seguir o padrão RESTful documentado.

### 5.1 Prefixo de versão

Todos os endpoints públicos da API devem iniciar com:

/api/v1/

Exemplos:

/api/v1/usuarios
/api/v1/ferramentas
/api/v1/emprestimos
/api/v1/ocorrencias
/api/v1/dispositivos-iot

### 5.2 Nomenclatura de recursos

Usar substantivos no plural:

Correto:

/api/v1/usuarios
/api/v1/ferramentas
/api/v1/emprestimos

Evitar:

/api/v1/criarUsuario
/api/v1/buscarFerramenta
/api/v1/realizarEmprestimo

### 5.3 Métodos HTTP

Usar métodos HTTP conforme a semântica:

- GET: consultar dados.
- POST: criar registros ou executar comandos que alteram estado.
- PUT: substituir ou atualizar recurso completo.
- PATCH: atualização parcial.
- DELETE: remover, quando permitido pela integridade referencial.

### 5.4 Status HTTP

Usar códigos coerentes:

- 200 OK: consulta ou alteração bem-sucedida.
- 201 Created: criação bem-sucedida.
- 204 No Content: operação concluída sem corpo de resposta.
- 400 Bad Request: entrada inválida.
- 401 Unauthorized: ausência ou invalidade de autenticação.
- 403 Forbidden: usuário autenticado sem permissão.
- 404 Not Found: recurso inexistente.
- 409 Conflict: conflito de estado ou regra de negócio.
- 422 Unprocessable Entity: dados semanticamente inválidos.
- 500 Internal Server Error: erro inesperado.

---

## 6. Padrão de payload JSON

Todos os payloads JSON devem seguir:

- Chaves em camelCase.
- Termos em português.
- Datas em ISO 8601.
- Evitar abreviações obscuras.
- Não expor senha, hash de senha, token interno, segredo, chave privada ou dados sensíveis.

Exemplo:

{
"idFerramenta": 105,
"nomeFerramenta": "Multímetro Digital",
"statusDisponivel": true,
"dataAtualizacao": "2026-02-12T10:30:00Z"
}

---

## 7. Padrão de erro da API

As falhas da API devem seguir o padrão inspirado em RFC 7807 — Problem Details for HTTP APIs.

Estrutura obrigatória:

{
"status": 403,
"titulo": "Acesso Negado",
"detalhe": "O técnico não possui permissão para liberar esta gaveta.",
"timestamp": "2026-02-12T10:30:00Z"
}

Campos obrigatórios:

- status: código HTTP.
- titulo: descrição curta do erro.
- detalhe: explicação clara.
- timestamp: data/hora do erro.

Campos opcionais úteis:

- path: endpoint que gerou o erro.
- codigo: código interno da regra de negócio.
- erros: lista de erros de validação por campo.

Não retornar stack trace para o cliente.

---

## 8. Segurança obrigatória

Segurança é prioridade máxima neste projeto.

### 8.1 JWT

Todas as requisições da API, exceto login, health check público ou endpoints explicitamente liberados, devem exigir JWT válido.

O backend deve:

- Validar assinatura do token.
- Validar expiração.
- Validar permissões/roles.
- Bloquear acesso não autorizado antes de chegar à regra de negócio.
- Não confiar em dados enviados pelo cliente para identificar usuário sem conferir o token.

### 8.2 Senhas

Senhas nunca devem ser armazenadas em texto puro.

Obrigatório:

- Usar algoritmo de hash seguro.
- Nunca retornar senha ou hash em DTOs.
- Nunca registrar senha em logs.
- Nunca versionar senhas reais.

### 8.3 Secrets

Nunca commitar:

- Tokens JWT reais.
- Senhas de banco.
- Credenciais MQTT.
- Chaves privadas.
- Arquivos .env com dados sensíveis.
- Credenciais de produção.
- Certificados privados.
- Dumps reais com dados sensíveis.

Usar variáveis de ambiente ou arquivos de configuração ignorados pelo Git.

### 8.4 Brute force

O serviço de autenticação deve considerar proteção contra brute force:

- Bloqueio temporário ou atraso progressivo após múltiplas tentativas inválidas.
- Não revelar se o erro foi usuário inexistente ou senha incorreta.
- Registrar tentativas suspeitas.

### 8.5 Autorização

Antes de executar ações críticas, validar permissões.

Ações críticas incluem:

- Liberar gaveta.
- Registrar empréstimo.
- Registrar devolução.
- Alterar status de ferramenta.
- Gerenciar usuários.
- Gerenciar dispositivos IoT.
- Excluir registros.
- Visualizar logs ou auditorias.
- Alterar dados administrativos.

---

## 9. Regras de banco de dados

O banco principal é MySQL.

### 9.1 Integridade referencial

O banco deve garantir relacionamento consistente entre usuários, técnicos, gestores, almoxarifes, ferramentas, empréstimos, devoluções, ocorrências, dispositivos IoT e logs de auditoria.

Não criar alterações que permitam dados órfãos.

### 9.2 Foreign Keys

Usar Foreign Keys para proteger integridade referencial.

Não remover constraints existentes sem justificativa clara.

Não excluir registros históricos importantes se isso quebrar rastreabilidade.

### 9.3 Histórico e auditoria

Registros históricos devem ser preservados.

Logs de movimentação devem manter:

- Quem executou a ação.
- Quando executou.
- O que foi alterado.
- Qual ferramenta ou recurso foi afetado.
- Origem da ação quando aplicável.

Logs de auditoria devem ter caráter imutável sempre que possível.

### 9.4 Migrations

Se o projeto usar Flyway ou Liquibase:

- Criar migrations versionadas.
- Nunca editar migration já aplicada em produção.
- Criar nova migration para alterações incrementais.
- Incluir rollback quando a ferramenta/padrão do projeto suportar.

Se o projeto ainda não usa ferramenta de migration:

- Não alterar schema de forma implícita sem documentar.
- Preferir scripts SQL claros e reprodutíveis.
- Manter compatibilidade com o modelo existente.

---

## 10. Regras de MQTT e IoT

O backend não deve se comunicar diretamente com o ESP32.

A comunicação deve ocorrer via Broker MQTT, como Mosquitto.

### 10.1 Fluxo esperado

Fluxo típico de retirada:

1. Mobile envia requisição REST ao backend solicitando ferramenta.
2. Backend valida regra de negócio.
3. Backend publica comando no Broker MQTT.
4. ESP32 recebe comando.
5. ESP32 aciona LED RGB e solenoide.
6. Técnico retira a ferramenta.
7. Leitor QR Code envia leitura ao ESP32.
8. ESP32 publica a leitura no Broker MQTT.
9. Backend consome a mensagem.
10. Backend valida QR Code, ferramenta e solicitação.
11. Backend atualiza o MySQL.
12. Backend registra auditoria.

### 10.2 Validação de mensagens MQTT

Todas as mensagens MQTT recebidas devem ser validadas quanto a:

- Formato JSON.
- Campos obrigatórios.
- Tipo da mensagem.
- Timestamp.
- Identificação do dispositivo.
- Origem cadastrada.
- Compatibilidade com estado atual da ferramenta.
- Compatibilidade com fluxo de negócio.

Mensagens inválidas devem ser ignoradas ou rejeitadas com log adequado.

Nunca executar comandos sensíveis vindos de dispositivo não cadastrado.

### 10.3 Resiliência MQTT

O sistema deve considerar:

- Reconexão automática ao broker.
- Tratamento de falhas temporárias.
- Logs para falha de publicação.
- Logs para falha de consumo.
- Timeout de operações críticas.
- Evitar travar fluxo HTTP por falha permanente de hardware.

### 10.4 Performance IoT

Requisitos importantes:

- Comando de abertura da trava solenoide deve ser processado rapidamente após validação.
- Latência MQTT deve ser baixa em condições normais.
- Evitar operações pesadas no caminho crítico de abertura da gaveta.
- Não bloquear threads de requisição desnecessariamente.
- Evitar múltiplas publicações redundantes para o mesmo comando.

---

## 11. Estado seguro de hardware

O sistema deve respeitar o princípio de fail-safe.

Em falha crítica:

- A trava solenoide deve permanecer fechada por padrão.
- O backend não deve liberar ferramenta sem validação.
- Dispositivo desconhecido não deve receber comando de abertura.
- Erro de autenticação nunca deve acionar hardware.
- Erro de regra de negócio nunca deve acionar hardware.
- Falha de QR Code deve resultar em recusa segura.

---

## 12. Domínio principal do sistema

O agente deve preservar as regras de negócio associadas a estes domínios:

### 12.1 Usuários

Possíveis perfis:

- Técnico.
- Almoxarife.
- Gestor.
- Administrador, se existir no código.

Regras:

- Usuários devem autenticar antes de usar recursos protegidos.
- Permissões devem limitar ações por perfil.
- Dados sensíveis não devem ser expostos.

### 12.2 Ferramentas

O sistema controla ferramentas industriais.

Estados possíveis podem incluir, conforme implementação existente:

- Disponível.
- Emprestada.
- Em manutenção.
- Danificada.
- Descartada.
- Indisponível.

Não alterar nomes de status sem verificar impacto em frontend, mobile, banco e documentação.

### 12.3 Empréstimos

O fluxo de empréstimo deve:

- Validar usuário solicitante.
- Validar ferramenta.
- Validar disponibilidade.
- Registrar data/hora.
- Atualizar status da ferramenta.
- Registrar auditoria.
- Integrar com IoT quando necessário.

### 12.4 Devoluções

O fluxo de devolução deve:

- Validar empréstimo ativo.
- Validar ferramenta devolvida.
- Registrar data/hora.
- Atualizar status da ferramenta.
- Registrar ocorrência se houver dano.
- Registrar auditoria.

### 12.5 Ocorrências

Ocorrências podem envolver dano, manutenção, perda, descarte, falha de leitura, problema de dispositivo e divergência em devolução.

Devem ser rastreáveis.

### 12.6 Dispositivos IoT

Somente dispositivos previamente cadastrados podem participar do fluxo.

Cada dispositivo deve possuir identificação única.

Dispositivos não cadastrados devem ser ignorados, bloqueados ou tratados como evento suspeito.

---

## 13. Logs e auditoria

O sistema deve registrar logs relevantes sem expor dados sensíveis.

### 13.1 Logs obrigatórios

Registrar:

- Erros de autenticação.
- Falhas de autorização.
- Falhas de comunicação MQTT.
- Mensagens MQTT inválidas.
- Comandos enviados ao hardware.
- Empréstimos.
- Devoluções.
- Ocorrências.
- Alterações administrativas.
- Erros inesperados de servidor.

### 13.2 Não registrar

Nunca registrar:

- Senhas.
- Hashes de senha.
- Tokens JWT completos.
- Secrets.
- Credenciais de banco.
- Credenciais MQTT.
- Dados pessoais desnecessários.

Se for necessário logar token para diagnóstico, mascarar.

---

## 14. Swagger / OpenAPI

Toda nova rota deve ser documentada.

A documentação deve incluir:

- Descrição do endpoint.
- Método HTTP.
- Path.
- Parâmetros.
- Request body.
- Response body.
- Códigos de erro relevantes.
- Regras de autenticação.
- Exemplos quando útil.

A API deve permanecer testável via Swagger/OpenAPI em ambiente de desenvolvimento.

---

## 15. Validações

Usar validações explícitas em DTOs e Services.

Exemplos:

- @NotNull
- @NotBlank
- @Size
- @Email
- @Pattern
- @Positive
- @FutureOrPresent
- @PastOrPresent

Validações de regra de negócio devem permanecer no Service.

Não confiar apenas no frontend ou mobile.

---

## 16. Tratamento de exceções

Preferir tratamento centralizado com @ControllerAdvice.

Criar exceções específicas de domínio quando necessário, por exemplo:

- RecursoNaoEncontradoException
- AcessoNegadoException
- RegraDeNegocioException
- FerramentaIndisponivelException
- DispositivoNaoAutorizadoException
- MensagemMqttInvalidaException

Evitar lançar RuntimeException genérica em regra de negócio.

---

## 17. Transações

Usar @Transactional em operações que alteram múltiplos dados relacionados.

Exemplos:

- Criar empréstimo + atualizar status da ferramenta + registrar auditoria.
- Registrar devolução + atualizar empréstimo + atualizar ferramenta + registrar ocorrência.
- Alterar dispositivo + registrar log administrativo.

Operações somente leitura podem usar @Transactional(readOnly = true).

Evitar transações longas envolvendo chamadas externas MQTT.

Quando possível:

1. Validar regra de negócio.
2. Persistir estado necessário.
3. Publicar evento/comando MQTT.
4. Registrar resultado.

Se a ordem tiver impacto crítico, justificar no código ou na descrição da alteração.

---

## 18. Performance

Requisitos relevantes:

- Telas Web e Mobile devem carregar dados de inventário rapidamente.
- Operações IoT devem ter baixa latência.
- Evitar consultas N+1.
- Usar paginação em listagens.
- Evitar retornar grandes volumes sem filtro.
- Evitar lógica pesada dentro de Controllers.
- Evitar operações bloqueantes no caminho crítico.

Listagens públicas ou administrativas devem preferir paginação:

?page=0&size=20&sort=nome,asc

---

## 19. Docker e ambiente

O sistema deve ser capaz de executar em containers Docker.

Ao alterar configurações:

- Preservar compatibilidade com Docker.
- Não hardcodar caminhos locais.
- Não depender de configuração exclusiva da máquina do desenvolvedor.
- Usar variáveis de ambiente.
- Documentar novas variáveis necessárias.

Exemplos de variáveis esperadas:

DB_HOST
DB_PORT
DB_NAME
DB_USER
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION
MQTT_BROKER_URL
MQTT_USERNAME
MQTT_PASSWORD

Não inserir valores reais no repositório.

---

## 20. Padrões Java e Spring Boot

Seguir convenções Java.

### 20.1 Nomenclatura

Classes:

UsuarioController
FerramentaService
EmprestimoRepository
DispositivoIotService

Métodos:

buscarPorId
listarTodos
criarEmprestimo
registrarDevolucao
validarPermissao

Variáveis:

idFerramenta
nomeUsuario
dataEmprestimo
statusFerramenta

Constantes:

TEMPO_EXPIRACAO_TOKEN
TOPICO_COMANDO_GAVETA
STATUS_DISPONIVEL

### 20.2 Idioma

Preferir português para nomes de domínio do projeto, mantendo consistência com documentação e código existente.

Não misturar português e inglês no mesmo conceito, exceto termos técnicos consolidados como Controller, Service, Repository, DTO, JWT, MQTT, Swagger, OpenAPI e Entity.

### 20.3 Injeção de dependência

Preferir injeção por construtor.

Evitar @Autowired em campos.

---

## 21. DTOs, mappers e entidades

Não expor entidades diretamente se já existir padrão de DTO.

Preferir:

- RequestDTO para entrada.
- ResponseDTO para saída.
- Mapper dedicado quando houver conversão repetida.

Exemplo:

CriarFerramentaRequest
FerramentaResponse
AtualizarFerramentaRequest
FerramentaMapper

Não retornar campos internos, como:

- senha
- senhaHash
- tokenInterno
- credenciais
- secret
- deletedAt, se for controle interno
- Informações técnicas sensíveis de dispositivos

---

## 22. Testes

Sempre que alterar regra de negócio, criar ou atualizar testes.

Priorizar:

- Testes unitários de Service.
- Testes de Controller quando alterar endpoints.
- Testes de Repository quando criar queries personalizadas.
- Testes de integração quando alterar fluxo completo.
- Testes para autenticação e autorização.
- Testes para validação de mensagens MQTT.
- Testes para casos de erro.

Ferramentas esperadas no projeto/documentação:

- JUnit
- Mockito
- Spring Boot Test
- Postman para validação manual de endpoints
- MySQL Workbench para conferência manual quando necessário

### 22.1 Casos mínimos para novas funcionalidades

Para cada nova feature relevante, testar:

- Caminho feliz.
- Entrada inválida.
- Recurso inexistente.
- Usuário sem permissão.
- Estado de negócio inválido.
- Falha de integração, quando aplicável.

---

## 23. Branches e Git

Não trabalhar diretamente na main, salvo instrução explícita do responsável.

Usar branches descritivas:

feature/crud-dispositivos-iot
fix/validacao-emprestimo-ferramenta
docs/swagger-emprestimos
test/service-emprestimos
refactor/camada-service-ferramentas

Commits devem ser pequenos e objetivos.

Mensagem recomendada:

feat: adiciona validação de dispositivo IoT cadastrado
fix: corrige status da ferramenta ao registrar devolução
docs: atualiza documentação Swagger de empréstimos
test: adiciona testes unitários para EmprestimoService
refactor: separa regra de negócio do controller de ferramentas

---

## 24. Restrições para o Codex

O Codex deve seguir estas regras obrigatórias:

### 24.1 Antes de alterar

Antes de modificar arquivos, o Codex deve:

1. Ler os arquivos relacionados.
2. Entender o padrão atual do projeto.
3. Verificar se já existe implementação semelhante.
4. Preservar estilo e arquitetura existentes.
5. Identificar impactos em API, banco, segurança e testes.

### 24.2 Durante a alteração

O Codex deve:

- Fazer alterações pequenas e revisáveis.
- Evitar reescrever grandes partes sem necessidade.
- Não remover código funcional sem justificativa.
- Não alterar contratos públicos sem avisar.
- Não quebrar compatibilidade com frontend/mobile sem necessidade.
- Não ignorar testes existentes.
- Não mascarar erro removendo validação.
- Não comentar código morto como solução final.

### 24.3 Depois da alteração

O Codex deve informar:

- Arquivos alterados.
- O que mudou.
- Por que mudou.
- Como testar.
- Riscos ou pontos de atenção.
- Testes executados.
- Testes que não conseguiu executar.

---

## 25. Operações proibidas sem confirmação humana

O Codex não deve executar sem confirmação explícita:

- Apagar tabelas.
- Apagar migrations.
- Remover autenticação.
- Desativar validação JWT.
- Expor endpoint sensível publicamente.
- Remover criptografia/hash de senha.
- Alterar regra de autorização.
- Alterar estrutura de banco de forma destrutiva.
- Remover logs de auditoria.
- Excluir histórico de empréstimos/devoluções.
- Alterar status ou enum usado por frontend/mobile.
- Trocar dependências principais.
- Atualizar versão major de framework.
- Reformatar o projeto inteiro.
- Renomear pacotes centrais.
- Alterar branch principal.
- Fazer force push.
- Inserir secrets no código.
- Criar mocks que escondam falhas reais em produção.

---

## 26. Segurança contra regressões

Ao corrigir bug, o Codex deve:

1. Reproduzir mentalmente ou por teste o problema.
2. Identificar a causa raiz.
3. Corrigir no ponto correto.
4. Adicionar teste cobrindo o caso.
5. Garantir que o comportamento anterior esperado continue funcionando.

Não corrigir apenas sintoma visual ou retorno superficial.

---

## 27. Compatibilidade com Web e Mobile

Como Web e Mobile consomem a API:

- Não alterar nomes de campos JSON sem necessidade.
- Não remover campos de resposta sem versionamento.
- Não mudar formato de datas.
- Não alterar status HTTP sem avaliar impacto.
- Não mudar endpoints sem manter compatibilidade ou documentar migração.
- Não retornar entidade completa se antes retornava DTO controlado.

Toda mudança de contrato deve ser documentada.

---

## 28. Regras para autenticação e roles

Antes de liberar uma ação, verificar:

- Usuário autenticado.
- Token válido.
- Token não expirado.
- Role/perfil permitido.
- Recurso solicitado pertence ao contexto permitido, quando aplicável.

Exemplos de restrição:

- Técnico pode solicitar retirada/devolução conforme regra.
- Almoxarife pode gerenciar ferramentas e ocorrências conforme regra.
- Gestor pode visualizar relatórios e administrar cadastros conforme regra.
- Administrador, se existir, pode gerenciar configurações globais.

Não assumir permissões se não estiverem claras no código ou documentação.

---

## 29. Regras para endpoints de hardware

Endpoints ou services que acionam hardware devem ser tratados como críticos.

Antes de acionar MQTT para abertura de gaveta:

1. Validar JWT.
2. Validar permissão do usuário.
3. Validar ferramenta.
4. Validar disponibilidade.
5. Validar dispositivo/gaveta vinculada.
6. Registrar intenção ou evento, se o fluxo exigir.
7. Publicar comando MQTT.
8. Registrar resultado ou falha.
9. Retornar resposta clara ao cliente.

Nunca acionar solenoide apenas com base em parâmetro enviado pelo cliente.

---

## 30. Regras para QR Code

Leituras de QR Code devem ser tratadas como entrada externa não confiável.

Validar:

- Formato.
- Tamanho.
- Identificador da ferramenta.
- Se a ferramenta existe.
- Se a ferramenta está vinculada ao fluxo esperado.
- Se o dispositivo que enviou a leitura é autorizado.
- Se a leitura está dentro de uma janela temporal válida, quando aplicável.

Não confiar em QR Code para autorizar operação sem validação no backend.

---

## 31. Regras para dispositivos IoT

Ao trabalhar com dispositivos IoT:

- Cada dispositivo deve ter identificação única.
- Dispositivo deve estar cadastrado.
- Dispositivo deve estar ativo.
- Dispositivo deve estar vinculado à gaveta/ferramenta correta, se houver esse modelo.
- Mensagens de dispositivo inativo devem ser rejeitadas ou ignoradas.
- Registrar tentativa suspeita.

---

## 32. Regras de status de ferramenta

Alterações de status devem ser consistentes.

Exemplos de transições que devem ser validadas:

- Disponível → Emprestada.
- Emprestada → Disponível.
- Disponível → Manutenção.
- Emprestada → Ocorrência/Danificada, conforme regra.
- Manutenção → Disponível.
- Danificada → Manutenção ou Descartada.

Não permitir transições impossíveis sem regra explícita.

Não excluir ferramenta com histórico de empréstimos se isso quebrar integridade referencial.

---

## 33. Regras para auditoria

Toda ação crítica deve gerar registro de auditoria.

A auditoria deve indicar:

- Usuário.
- Perfil.
- Ação.
- Recurso afetado.
- Data/hora.
- Resultado.
- Origem, quando aplicável: Web, Mobile, Backend, MQTT, ESP32.
- Detalhe técnico seguro, quando necessário.

Não permitir edição ou exclusão simples de auditoria, salvo processo administrativo documentado.

---

## 34. Configuração de ambiente

Não hardcodar ambiente.

Evitar:

String url = "jdbc:mysql://localhost:3306/toolhub";
String senha = "123456";
String broker = "tcp://localhost:1883";

Preferir:

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
mqtt.broker.url=${MQTT_BROKER_URL}

Fornecer valores seguros apenas em .env.example, nunca valores reais.

---

## 35. Boas práticas de dependências

Antes de adicionar dependência:

1. Verificar se já existe biblioteca equivalente no projeto.
2. Avaliar necessidade real.
3. Evitar dependências abandonadas.
4. Preferir dependências populares e mantidas.
5. Não adicionar bibliotecas pesadas para tarefas simples.
6. Evitar versões beta/alpha.
7. Não atualizar versão major sem aprovação.

---

## 36. Documentação obrigatória em alterações relevantes

Ao criar ou alterar funcionalidade, atualizar quando aplicável:

- Swagger/OpenAPI.
- README.
- .env.example.
- Scripts SQL ou migrations.
- Documentação técnica.
- Exemplos de request/response.
- Testes de Postman, se existirem no repositório.
- Comentários em pontos críticos.

---

## 37. Estilo de resposta esperado do Codex

Ao finalizar uma tarefa, responder em português do Brasil com:

Resumo:
- ...

Arquivos alterados:
- ...

Como testar:
- ...

Testes executados:
- ...

Pontos de atenção:
- ...

Se não executar testes, informar claramente:

Não executei os testes porque ...

Nunca afirmar que testou se não testou.

---

## 38. Quando pedir ajuda humana

Pedir validação humana quando:

- A documentação estiver ambígua.
- Código e Confluence divergirem em regra crítica.
- Alteração afetar segurança.
- Alteração afetar banco de dados de forma destrutiva.
- Alteração afetar contrato usado por Web/Mobile.
- Alteração envolver hardware físico.
- Alteração envolver deploy, produção ou credenciais.
- Testes não puderem ser executados.
- Houver risco de perda de histórico/auditoria.

---

## 39. Checklist antes de abrir Pull Request

Antes de considerar a tarefa concluída, verificar:

- [ ] Código compila.
- [ ] Testes relevantes foram criados ou atualizados.
- [ ] Testes existentes não foram quebrados.
- [ ] Endpoints seguem /api/v1/.
- [ ] DTOs usam camelCase.
- [ ] Erros seguem padrão Problem Details.
- [ ] JWT continua obrigatório onde necessário.
- [ ] Nenhum secret foi commitado.
- [ ] Logs não expõem dados sensíveis.
- [ ] Swagger/OpenAPI foi atualizado.
- [ ] Banco mantém integridade referencial.
- [ ] Fluxos críticos registram auditoria.
- [ ] MQTT valida origem e formato.
- [ ] Hardware permanece em estado seguro em caso de falha.
- [ ] README ou documentação foi atualizada se necessário.
- [ ] Alterações são pequenas e revisáveis.

---

## 40. Instruções específicas para tarefas comuns

### 40.1 Criar novo CRUD

Ao criar um CRUD:

1. Criar/validar Entity.
2. Criar Repository.
3. Criar DTOs de request e response.
4. Criar Mapper, se fizer sentido.
5. Criar Service com regras de negócio.
6. Criar Controller em /api/v1/{recurso}.
7. Criar tratamento de erros.
8. Criar testes.
9. Documentar no Swagger.
10. Garantir segurança por JWT.

### 40.2 Criar endpoint protegido

Ao criar endpoint protegido:

1. Confirmar role permitida.
2. Validar token JWT.
3. Validar entrada.
4. Executar regra no Service.
5. Retornar DTO.
6. Registrar auditoria se for ação crítica.
7. Documentar erros 401 e 403.

### 40.3 Criar integração MQTT

Ao criar ou alterar integração MQTT:

1. Validar tópico.
2. Validar payload.
3. Validar dispositivo.
4. Tratar falhas de conexão.
5. Implementar log seguro.
6. Evitar bloquear transações longas.
7. Criar teste unitário da lógica de validação.
8. Documentar formato da mensagem.

### 40.4 Alterar banco de dados

Ao alterar banco:

1. Verificar relacionamentos.
2. Preservar histórico.
3. Criar migration/script.
4. Evitar alteração destrutiva.
5. Atualizar Entity.
6. Atualizar Repository se necessário.
7. Atualizar testes.
8. Documentar mudança.

---

## 41. Exemplo de estrutura recomendada

A estrutura pode variar conforme o projeto existente, mas deve respeitar a separação:

src/main/java/.../
controller/
service/
repository/
model/
entity/
dto/
request/
response/
mapper/
config/
security/
exception/
mqtt/
audit/

Não reorganizar todo o projeto sem necessidade.

Seguir a estrutura real já existente no repositório.

---

## 42. Princípios finais

O Codex deve sempre priorizar:

1. Segurança.
2. Integridade dos dados.
3. Clareza da regra de negócio.
4. Compatibilidade com Web e Mobile.
5. Rastreabilidade.
6. Testabilidade.
7. Manutenibilidade.
8. Baixa complexidade.
9. Alterações pequenas.
10. Alinhamento com documentação do Confluence.

Em caso de dúvida, não improvisar regra crítica: explicar a dúvida e propor opções seguras.

---

## 43. Resumo de comandos úteis

Antes de sugerir comandos, verificar o gerenciador real do projeto.

Possíveis comandos para projetos Spring Boot com Maven:

./mvnw clean test
./mvnw spring-boot:run
./mvnw clean package

No Windows:

mvnw.cmd clean test
mvnw.cmd spring-boot:run
mvnw.cmd clean package

Se o projeto usar Gradle, preferir:

./gradlew test
./gradlew bootRun
./gradlew build

Não inventar comando se o wrapper não existir.

---

## 44. Observação final para agentes

Este arquivo deve ser carregado como contexto prioritário para qualquer tarefa do Codex neste repositório.

Qualquer alteração que viole este documento deve ser explicitamente justificada e revisada por uma pessoa responsável pelo projeto.
