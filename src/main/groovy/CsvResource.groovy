import org.supercsv.cellprocessor.Optional
import org.supercsv.cellprocessor.ParseDate
import org.supercsv.cellprocessor.ParseInt
import org.supercsv.cellprocessor.StrReplace
import org.supercsv.cellprocessor.ift.CellProcessor
import org.supercsv.io.CsvMapReader
import org.supercsv.prefs.CsvPreference

import java.nio.charset.Charset

class CsvResource {
  static final String fileName = "TYPER.csv"
  final String[] fields = ['nick', 'punkty', 'liga/zawody', 'data', 'sport', 'Kolumna1', 'link', 'null',
                           'Alternatywny nick', 'Uwagi']
  final CellProcessor[] parsers = [null, new ParseInt(), null, new ParseDate("dd.MM.yyyy"), null, null,
                                   new StrReplace("/\$", ""), new Optional(), new Optional(),
                                   new Optional()]
  FileLogger log

  CsvResource(FileLogger logger = null) {
    log = logger ?: FileLogger.getLogger("CsvResource")
  }

  HashSet<String> pullLinks() {
    try {
      FileReader fileReader = new FileReader(new File(fileName), Charset.availableCharsets()['Windows-1250'])
      fileReader.readLine()
      def reader = new CsvMapReader(fileReader, CsvPreference.EXCEL_PREFERENCE)
      Map<String, String> line
      def set = new HashSet<String>()
      while (line = reader.read(fields, parsers) as Map<String, String>) {
        set.add(line[TyperHeaders.LINK.getName()])
      }
      return set
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage())
    } catch (IOException e) {
      log.error(e.toString())
    } catch (Exception e) {
      log.error(e.toString())
    }
    return new HashSet<String>()
  }
}
