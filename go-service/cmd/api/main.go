package main

import (
	"context"
	"log/slog"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/Rom-haguko/fitness-app/go-service/internal/app"
	"github.com/Rom-haguko/fitness-app/go-service/internal/config"
	"github.com/Rom-haguko/fitness-app/go-service/internal/logger"
)

func main() {
	ctx := context.Background()

	cfg := config.Load()
	log := logger.New(slog.LevelInfo, os.Stdout)

	application, err := app.New(ctx, cfg, log)
	if err != nil {
		log.Error("failed to initialize application", slog.Any("error", err))
		os.Exit(1)
	}

	go func() {
		if err := application.Run(); err != nil {
			log.Error("application stopped with error", slog.Any("error", err))
			os.Exit(1)
		}
	}()

	stop := make(chan os.Signal, 1)
	signal.Notify(stop, syscall.SIGINT, syscall.SIGTERM)
	<-stop

	shutdownCtx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := application.Shutdown(shutdownCtx); err != nil {
		log.Error("failed to shutdown application", slog.Any("error", err))
		os.Exit(1)
	}

	log.Info("application stopped")

}
