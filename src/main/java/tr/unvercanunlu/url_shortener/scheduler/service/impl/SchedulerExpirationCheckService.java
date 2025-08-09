package tr.unvercanunlu.url_shortener.scheduler.service.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import tr.unvercanunlu.url_shortener.config.AppConfig;
import tr.unvercanunlu.url_shortener.scheduler.job.ExpirationCheckJob;
import tr.unvercanunlu.url_shortener.scheduler.service.SchedulerService;

public class SchedulerExpirationCheckService implements SchedulerService {

  private final ThreadFactory threadFactory = job -> {
    Thread created = new Thread(job, AppConfig.EXPIRATION_CHECK_THREAD_NAME);
    created.setDaemon(true);
    return created;
  };

  private final AtomicReference<ScheduledFuture<?>> handle = new AtomicReference<>();

  private ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor(threadFactory);

  private Runnable schedulingJob;

  @Override
  public void reInitialize() {
    stopSchedulingJob();

    scheduledService = Executors.newSingleThreadScheduledExecutor(threadFactory);

    handle.set(null);
  }

  @Override
  public void initializeSchedulingJob() {
    // initialize
    schedulingJob = new ExpirationCheckJob();
  }

  @Override
  public void startSchedulingJob() {
    // validations
    checkShutdown();
    validateJob(schedulingJob);
    validateDuration();

    ScheduledFuture<?> current = handle.get();
    if (current != null && !current.isDone() && !current.isCancelled()) {
      return;
    }

    // start
    try {
      handle.set(
          scheduledService.scheduleAtFixedRate(schedulingJob, 0,
              AppConfig.EXPIRATION_CHECK_DURATION, AppConfig.EXPIRATION_CHECK_TIME_UNIT)
      );

    } catch (RejectedExecutionException e) {
      throw new IllegalStateException("Scheduler not accepting tasks!", e);
    }
  }

  @Override
  public void stopSchedulingJob() {
    cancelIfHandleExists();

    if (!scheduledService.isShutdown()) {
      // stop
      scheduledService.shutdown();
    }
  }

  @Override
  public void stopSchedulingJob(long duration, TimeUnit unit) {
    cancelIfHandleExists();

    if (!scheduledService.isShutdown()) {
      scheduledService.shutdown();

      try {
        if (!scheduledService.awaitTermination(duration, unit)) {
          scheduledService.shutdownNow();
        }

      } catch (InterruptedException e) {
        scheduledService.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
  }

  @Override
  public boolean isRunning() {
    ScheduledFuture<?> current = handle.get();

    return (current != null) && !current.isCancelled() && !current.isDone();
  }

  private void validateJob(Runnable job) {
    if (job == null) {
      throw new IllegalArgumentException("Job missing!");
    }
  }

  private void validateDuration() {
    if (AppConfig.EXPIRATION_CHECK_DURATION <= 0) {
      throw new IllegalArgumentException("Duration invalid: duration=%d".formatted(AppConfig.EXPIRATION_CHECK_DURATION));
    }

    if (AppConfig.EXPIRATION_CHECK_TIME_UNIT == null) {
      throw new IllegalArgumentException("Time Unit missing!");
    }
  }

  private void checkShutdown() {
    if (scheduledService.isShutdown()) {
      throw new IllegalStateException("Scheduler shutdown, please reinitialize again!");
    }
  }

  private void cancelIfHandleExists() {
    ScheduledFuture<?> previous = handle.getAndSet(null);
    if (previous != null) {
      previous.cancel(false);
    }
  }

}
