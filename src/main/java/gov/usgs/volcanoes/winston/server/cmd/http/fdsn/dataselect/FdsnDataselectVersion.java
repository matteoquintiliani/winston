package gov.usgs.volcanoes.winston.server.cmd.http.fdsn.dataselect;

import gov.usgs.net.NetTools;
import gov.usgs.volcanoes.winston.db.WinstonDatabase;
import gov.usgs.volcanoes.winston.server.WWS;
import gov.usgs.volcanoes.winston.server.cmd.http.fdsn.command.FdsnVersionCommand;

/**
 *
 * @author Tom Parker
 *
 */
public class FdsnDataselectVersion extends FdsnVersionCommand implements FdsnDataselectService {

  public FdsnDataselectVersion(final NetTools nt, final WinstonDatabase db, final WWS wws) {
    super(nt, db, wws);
    version = VERSION;
  }

  @Override
  public String getCommand() {
    return "/fdsnws/dataselect/1/version";
  }
}