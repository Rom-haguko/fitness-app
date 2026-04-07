# Этап сборки
FROM golang:1.22-alpine AS builder

WORKDIR /app

COPY . .

RUN go mod download
RUN go build -o go-service

FROM alpine:latest

WORKDIR /app
COPY --from=builder /app/go-service .

CMD ["./go-service"]
