# Этап сборки
FROM golang:1.22-alpine AS builder

WORKDIR /app

# Кешируем зависимости
COPY go.mod go.sum* ./
RUN go mod download

# Копируем исходники
COPY . .

# Собираем статический бинарник
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o go-service .

# Финальный образ
FROM alpine:latest

WORKDIR /app
COPY --from=builder /app/go-service /app/go-service
RUN chmod +x /app/go-service

CMD ["/app/go-service"]
