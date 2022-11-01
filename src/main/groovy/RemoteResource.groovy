import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import org.jsoup.select.Elements


/**
 * Odpowiedzialna za pobranie i przetwarzanie zdalnych zasobów.
 * Jej zależności to jakiś klient HTTP, żeby można było wykonać zapytanie do forum
 * i dalej procesowanie po wynikach wyszukiwania.*/
class RemoteResource {

  /** moje standardowe wyjscie **/
  FileLogger logger
  final int DELAY = 400 // opóźnienie pobierania kolejnych stron w milisekundach (ms)
  Document document

  RemoteResource(FileLogger log = null) {
    logger = log ?: FileLogger.getLogger("RemoteResource")
  }

  private static URL createSearchingUrl(int month, int year, int page) {
    String strMonth = (month < 10) ? "0${month}" : "${month}"
    def uri = new URI("https://pogrywamy.pl/search/?&q=Typowanie%20${strMonth}.${year}&page=${page}&search_and_or=or")
    return uri.toURL()
  }

  /** Zbiera liczbę wyników, aby wiadomo było czy jeszcze przechodzić na kolejną stronę.
   * Niestety nie ma zabezpieczenia czy pobrano dwa razy te same linki, założono że strona wyszukiwania
   * jest skanowana z góry na dół. *HINT: Ewentualnie można by wyniki posortować dodatkowym parametrem.
   * @return number of results
   */
  private int getNumberOfResults() {
    if (!document) return -1
    Document doc = document
    def element = doc.getElementsByAttributeValue('class', 'ipsType_sectionTitle')
    logger.info(element.text())
    def parts = element.text().split(' ')
    if (parts.size() != 3) {
      logger.error("Zbyt mała liczba części.")
      return -1
    } else {
      return parts[1].toInteger()
    }
  }

  /** zajmuje się wyciąganiem linków do wyszukanych stron **/
  private HashSet<String> extractLinks() {
    if (!document) return new HashSet<String>()
    def links = new HashSet<String>()
    Elements aElements = document.getElementsByTag('a')
    aElements.each {
      def href = it.attr('href')
      if (href.contains('pogrywamy.pl/topic')) {
        href = href.split("/\\?")[0]
        links.add(href)
      }
    }
    return links
  }

  /** Pobierz pojedynczą stronę wyszukiwania */
  private boolean loadDocument(int month, int year, int page) {
    def url = createSearchingUrl(month, year, page)
    OkHttpClient client = new OkHttpClient()
    try {
      Request request = new Request.Builder().url(url).build()
      def response = client.newCall(request).execute()
      if (200 == response.code()) {
        Parser parser = Parser.htmlParser()
        document = parser.parseInput(response.body().string(), url.toURI().toString())
        //logger.info(response.body().string())
      }
    } catch (IOException e) {
      logger.error(e.getMessage())
      return false
    }
    return true
  }

  HashSet<String> pullResults(int month, int year) {
    HashSet<String> links = new HashSet<>()
    if (!loadDocument(month, year, 1)) {
      logger.error("Nie udało się załadować pierwszej strony.")
      throw new errors.FailedLoadException()
    } else {
      links.addAll(extractLinks())
      int numberOfResults = getNumberOfResults()
      logger.info("Number of results ${numberOfResults}")
      int i = 2
      while (links.size() < numberOfResults) {
        if (!loadDocument(month, year, i)) {
          logger.error("Wystąpił błąd przy ładowaniu strony numer ${i}.")
          break
        } else {
          links.addAll(extractLinks())
          i += 1
          sleep(DELAY)
        }
      }
    }
    return links
  }
}
