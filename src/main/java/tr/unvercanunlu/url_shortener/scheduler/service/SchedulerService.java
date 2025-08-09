package tr.unvercanunlu.url_shortener.scheduler.service;

import java.util.concurrent.TimeUnit;

public interface SchedulerService {

  void reInitialize();

  void initializeSchedulingJob();

  void startSchedulingJob();

  void stopSchedulingJob();

  void stopSchedulingJob(long duration, TimeUnit unit);

  boolean isRunning();

}
