import javax.management.InvalidAttributeValueException

FileLogger log = FileLogger.getLogger("main")
FileLogger dataLog = FileLogger.getLogger("mainData")
boolean good
int m, y
final String TOPIC_BASE = 'https://pogrywamy.pl/topic/'

log.info("SCRIPT STARTING.")
good = false
try {
  if (args.length < 2) throw new Exception("Za mała liczba argumentów")
  m = Integer.parseInt(args[0])
  y = Integer.parseInt(args[1])
  if (m <= 0 || m > 12) {
    throw new InvalidAttributeValueException()
  }
  good = true
} catch (NumberFormatException ignore) {
  log.error("Podane argumenty: miesiac, rok są nie poprawne.")
} catch (InvalidAttributeValueException ignore) {
  log.error("Wartość argumentu miesiąca jest niepoprawna.")
} catch (Exception e) {
  log.error(e.getMessage())
}
if (!good) System.exit(-1)
csv = new CsvResource(dataLog)
remote = new RemoteResource()
links = csv.pullLinks()
try {
  def remoteLinks = remote.pullResults(m, y)
  if (remoteLinks.size() == 0) System.exit(-2)
  Closure<String> pullLinkId = (String link) -> { link.substring(TOPIC_BASE.length(), link.indexOf('-')) }
  dataLog.info("### BRAKUJACE LINKI")
  def founded = remoteLinks.findAll {
    def id = pullLinkId(it)
    if (!links.find({ it.contains(id) })) {
      dataLog.info("link ${it} nie został znaleziony w csv.")
    } else {
      return true
    }
  }
  log.info("Znalezionych linkow jest ${founded.size()}")
  dataLog.info("### BRAKUJACE LINKI ###")
} catch (errors.FailedLoadException ignore) {
  System.exit(-3)
} catch (Exception e) {
  log.error(e.getMessage())
  System.exit(-4)
}

log.info("SCRIPT ENDED")
System.exit(0)