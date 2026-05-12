package db

import (
	"context"
	"fmt"
	"time"

	"github.com/Rom-haguko/fitness-app/go-service/internal/config"
	"github.com/jackc/pgx/v5/pgxpool"
)

func NewPostgresPool(ctx context.Context, cfg config.Config) (*pgxpool.Pool, error) {
	poolConfig, err := pgxpool.ParseConfig(cfg.DSN())
	if err != nil {
		return nil, fmt.Errorf("parse postgres config: %w", err)
	}

	poolConfig.MaxConns = 10
	poolConfig.MinConns = 2
	poolConfig.MaxConnLifetime = 30 * time.Minute
	poolConfig.MaxConnIdleTime = 5 * time.Minute
	poolConfig.HealthCheckPeriod = 1 * time.Minute

	var lastErr error

	for attempt := 1; attempt <= 10; attempt++ {
		pool, err := pgxpool.NewWithConfig(ctx, poolConfig)
		if err != nil {
			lastErr = err
			time.Sleep(2 * time.Second)
			continue
		}

		pingCtx, cancel := context.WithTimeout(ctx, 5*time.Second)
		err = pool.Ping(pingCtx)
		cancel()

		if err == nil {
			return pool, nil
		}

		lastErr = err
		pool.Close()

		time.Sleep(2 * time.Second)
	}

	return nil, fmt.Errorf("connect postgres after retries: %w", lastErr)
}
