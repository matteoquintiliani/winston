package gov.usgs.volcanoes.winston.server.cmd;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

import gov.usgs.net.NetTools;
import gov.usgs.plot.data.Wave;
import gov.usgs.util.CodeTimer;
import gov.usgs.util.CurrentTime;
import gov.usgs.util.Util;
import gov.usgs.volcanoes.core.util.UtilException;
import gov.usgs.volcanoes.winston.db.WinstonDatabase;
import gov.usgs.volcanoes.winston.server.WWS;
import gov.usgs.volcanoes.winston.server.WWSCommandString;

/**
 *
 * Example command string:
 * GETWAVERAW: GS AUI EHZ AV -- 284802563.447000 284802683.447000 1
 *
 * Compress wave before returning
 * Not supported by wave_serverV
 *
 * @author Dan Cervelli
 */
public class GetWaveRawCommand extends BaseCommand {
  public GetWaveRawCommand(final NetTools nt, final WinstonDatabase db, final WWS wws) {
    super(nt, db, wws);
  }

  public void doCommand(final Object info, final SocketChannel channel) {
    final CodeTimer ct = new CodeTimer("GetWaveRaw");

    final WWSCommandString cmd = new WWSCommandString((String) info);
    if (!cmd.isLegalSCNLTT(9))
      return; // malformed command

    double et = cmd.getT2(true);
    et = timeOrMaxDays(et);

    double st = cmd.getT1(true);
    st = timeOrMaxDays(st);

    et = Math.min(et, CurrentTime.getInstance().nowJ2K() - wws.getEmbargo());
    Wave wave = null;
    if (st < et) {
      try {
        wave = data.getWave(cmd.getWinstonSCNL(), st, et, 0);
      } catch (final UtilException e) {
      }
    }
    ct.stop();

    // Did it take too long to gather the data?
    if (wws.getSlowCommandTime() > 0 && ct.getRunTimeMillis() > wws.getSlowCommandTime() * .75)
      wws.log(Level.INFO,
          String.format("slow db query (%1.2f ms) GETWAVERAW " + cmd.getWinstonSCNL() + " " + st
              + " -> " + et + " (" + decimalFormat.format(et - st) + ") ", ct.getRunTimeMillis()),
          channel);

    ByteBuffer bb = null;
    if (wave != null && wave.numSamples() > 0)
      bb = (ByteBuffer) wave.toBinary().flip();

    final boolean compress = cmd.getInt(8) == 1;

    ct.start();
    final int bytes = writeByteBuffer(cmd.getID(), bb, compress, channel);
    ct.stop();

    // Did it take too long to deliver the data?
    if (wws.getSlowCommandTime() > 0 && ct.getRunTimeMillis() > wws.getSlowCommandTime() * .75)
      wws.log(Level.INFO,
          String.format("slow network (%1.2f ms) GETWAVERAW " + cmd.getWinstonSCNL() + " " + st
              + " -> " + et + " (" + decimalFormat.format(et - st) + ") ", ct.getRunTimeMillis()),
          channel);

    final String time = Util.j2KToDateString(st) + " - " + Util.j2KToDateString(et);
    wws.log(Level.FINER, "GETWAVERAW " + cmd.getWinstonSCNL() + ": " + time + ", " + bytes
        + " bytes. (" + (String) info, channel);
  }
}
