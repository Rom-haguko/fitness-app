package app

import (
	"context"
	"fmt"
	"log/slog"
	"net/http"
	"time"

	"github.com/Rom-haguko/fitness-app/go-service/internal/config"
	"github.com/Rom-haguko/fitness-app/go-service/internal/db"
	"github.com/Rom-haguko/fitness-app/go-service/internal/httpserver"
	"github.com/jackc/pgx/v5/pgxpool"
)

type App struct {
	Config config.Config
	Logger *slog.Logger
	DB     *pgxpool.Pool
	Server *http.Server
}

func New(ctx context.Context, cfg config.Config, log *slog.Logger) (*App, error) {
	pool, err := db.NewPostgresPool(ctx, cfg)
	if err != nil {
		return nil, fmt.Errorf("new postgres pool: %w", err)
	}

	router := httpserver.NewRouter(log)

	server := &http.Server{
		Addr:              cfg.Addr(),
		Handler:           router,
		ReadHeaderTimeout: 5 * time.Second,
		ReadTimeout:       10 * time.Second,
		WriteTimeout:      15 * time.Second,
		IdleTimeout:       60 * time.Second,
	}

	return &App{
		Config: cfg,
		Logger: log,
		DB:     pool,
		Server: server,
	}, nil
}

func (a *App) Run() error {
	a.Logger.Info("starting http server",
		slog.String("addr", a.Server.Addr),
		slog.String("env", a.Config.AppEnv),
	)

	if err := a.Server.ListenAndServe(); err != nil {
		return fmt.Errorf("listen and serve: %w", err)
	}

	return nil
}

func (a *App) Shutdown(ctx context.Context) error {
	a.Logger.Info("shutting down application")

	if a.Server != nil {
		if err := a.Server.Shutdown(ctx); err != nil {
			return fmt.Errorf("shutdown http server: %w", err)
		}
	}

	if a.DB != nil {
		a.DB.Close()
	}

	return nil
}
