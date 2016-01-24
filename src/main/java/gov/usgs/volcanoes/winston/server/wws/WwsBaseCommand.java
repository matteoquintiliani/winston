/**
 * I waive copyright and related rights in the this work worldwide through the CC0 1.0 Universal
 * public domain dedication. https://creativecommons.org/publicdomain/zero/1.0/legalcode
 */

package gov.usgs.volcanoes.winston.server.wws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import gov.usgs.volcanoes.core.util.UtilException;
import gov.usgs.volcanoes.winston.server.BaseCommand;
import gov.usgs.volcanoes.winston.server.MalformedCommandException;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author Dan Cervelli
 */
abstract public class WwsBaseCommand extends BaseCommand implements WwsCommand {
  private static final Logger LOGGER = LoggerFactory.getLogger(WwsBaseCommand.class);

  protected final static int ONE_HOUR_S = 60 * 60;
  protected final static int ONE_DAY_S = 24 * ONE_HOUR_S;

  protected int maxDays;

  public WwsBaseCommand() {
    super();
  }

  public void setMaxDays(int maxDays) {
    this.maxDays = maxDays;
  }

  /**
   * Do the work. Return response to the client.
   * 
   * @throws MalformedCommandException
   * @throws UtilException
   */
  public void respond(ChannelHandlerContext ctx, WwsCommandString req)
      throws MalformedCommandException, UtilException {
    LOGGER.info("Recieved command: {}", req.getCommandString());
    doCommand(ctx, req);
  }

  protected static DecimalFormat getDecimalFormat() throws UtilException {
    try {
      DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();
      decimalFormat.setMaximumFractionDigits(3);
      decimalFormat.setGroupingUsed(false);
      return decimalFormat;
    } catch (ClassCastException ex) {
      throw new UtilException("Unable to cast NumberFormat to DecimalFormat.");
    }
  }
}
