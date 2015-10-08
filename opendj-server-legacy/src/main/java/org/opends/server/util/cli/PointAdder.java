/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at legal-notices/CDDLv1_0.txt
 * or http://forgerock.org/license/CDDLv1.0.html.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at legal-notices/CDDLv1_0.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2010 Sun Microsystems, Inc.
 *      Portions Copyright 2014-2016 ForgeRock AS
 */
package org.opends.server.util.cli;

import org.forgerock.i18n.LocalizableMessageBuilder;
import org.opends.quicksetup.util.PlainTextProgressMessageFormatter;
import org.opends.quicksetup.util.ProgressMessageFormatter;

import com.forgerock.opendj.cli.ConsoleApplication;

/** Class used to add points periodically to the end of the output. */
public class PointAdder implements Runnable
{
  private final ConsoleApplication app;
  private Thread t;
  private boolean stopPointAdder;
  private boolean pointAdderStopped;
  private long periodTime = DEFAULT_PERIOD_TIME;
  private final ProgressMessageFormatter formatter;

  /** The default period time used to write points in the output. */
  public static final long DEFAULT_PERIOD_TIME = 3000;

  /**
   * Default constructor.
   *
   * @param app
   *          The console application to be used. Creates a PointAdder that
   *          writes to the standard output with the default period time.
   */
  public PointAdder(ConsoleApplication app)
  {
    this(app, DEFAULT_PERIOD_TIME, new PlainTextProgressMessageFormatter());
  }

  /**
   * Default constructor.
   *
   * @param app
   *          The console application to be used.
   * @param periodTime
   *          The time between printing two points.
   * @param formatter
   *          The text formatter.
   */
  public PointAdder(ConsoleApplication app, long periodTime, ProgressMessageFormatter formatter)
  {
    this.app = app;
    this.periodTime = periodTime;
    this.formatter = formatter;
  }

  /** Starts the PointAdder: points are added at the end of the logs periodically. */
  public void start()
  {
    LocalizableMessageBuilder mb = new LocalizableMessageBuilder();
    mb.append(formatter.getSpace());
    for (int i=0; i< 5; i++)
    {
      mb.append(formatter.getFormattedPoint());
    }
    app.print(mb.toMessage());
    t = new Thread(this);
    t.start();
  }

  /** Stops the PointAdder: points are no longer added at the end of the logs periodically. */
  public synchronized void stop()
  {
    stopPointAdder = true;
    while (!pointAdderStopped)
    {
      try
      {
        t.interrupt();
        // To allow the thread to set the boolean.
        Thread.sleep(100);
      }
      catch (Throwable t)
      {
      }
    }
  }

  @Override
  public void run()
  {
    while (!stopPointAdder)
    {
      try
      {
        Thread.sleep(periodTime);
        app.print(formatter.getFormattedPoint());
      }
      catch (Throwable t)
      {
      }
    }
    pointAdderStopped = true;
  }
}
