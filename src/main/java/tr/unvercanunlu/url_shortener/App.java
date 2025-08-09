package tr.unvercanunlu.url_shortener;

import java.util.concurrent.TimeUnit;
import tr.unvercanunlu.url_shortener.scheduler.service.SchedulerService;
import tr.unvercanunlu.url_shortener.scheduler.service.impl.SchedulerExpirationCheckService;

public final class App {

  private static final SchedulerService schedulerService = new SchedulerExpirationCheckService();
  private static final Runnable shutdownJob = () -> schedulerService.stopSchedulingJob(10, TimeUnit.SECONDS);

  public static void main(String[] args) {
    // initialize
    schedulerService.initializeSchedulingJob();

    // start
    schedulerService.startSchedulingJob();

    // graceful shutdown or kill
    Thread shutdownThread = new Thread(shutdownJob, "shutdown-hook");
    Runtime.getRuntime().addShutdownHook(shutdownThread);

    // keep main thread alive till stopping scheduler service
    try {
      Thread.sleep(Long.MAX_VALUE);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

}
