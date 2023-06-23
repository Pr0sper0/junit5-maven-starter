package com.val.junit;

import com.val.junit.service.UserServiceTest;
import java.io.PrintWriter;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

public class TestLauncher {

  public static void main(String[] args) {
    Launcher launcher = LauncherFactory.create();

    SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();

    //launcher.registerTestExecutionListeners(summaryGeneratingListener);

    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
        .request()
        .selectors(DiscoverySelectors.selectPackage("com.val.junit.service"))
        .build();

    launcher.execute(request, summaryGeneratingListener);


    try (var printWriter = new PrintWriter(System.out)) {
      summaryGeneratingListener.getSummary().printTo(printWriter);
    }

  }

}
