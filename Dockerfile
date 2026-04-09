# ==========================================
# Estágio 1: Build (Compilação e Empacotamento)
# ==========================================
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /build

# Copia os arquivos do Maven Wrapper e o pom.xml primeiro
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dá permissão de execução ao wrapper
RUN chmod +x ./mvnw

# Baixa as dependências offline (Isso cria uma camada de cache no Docker)
# Se o pom.xml não mudar, o Docker não fará o download da internet novamente
RUN ./mvnw dependency:go-offline -B

# Copia o código-fonte
COPY src src

# Compila o projeto e gera o .jar (ignorando os testes para acelerar o deploy)
RUN ./mvnw clean package -DskipTests

# ==========================================
# Estágio 2: Runtime (Execução - Imagem Final)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Criação de um usuário não-root para rodar a aplicação (Boa prática de segurança)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia apenas o arquivo .jar gerado do estágio de build
COPY --from=build /build/target/*.jar app.jar

# Informa a porta que a aplicação escutará
EXPOSE 8080

# Comando para iniciar a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]