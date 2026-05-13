FROM golang:1.22-alpine AS builder

WORKDIR /app

COPY go.mod go.sum* ./
RUN go mod download

COPY . .

RUN CGO_ENABLED=0 GOOS=linux go build -o go-service ./cmd/api

FROM alpine:latest

WORKDIR /app

COPY --from=builder /app/go-service /app/go-service

EXPOSE 8081

CMD ["/app/go-service"]