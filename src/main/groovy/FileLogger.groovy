import java.text.SimpleDateFormat

class FileLogger {
  enum Level {
    ERROR("ERROR"),
    INFO("INFO")

    String type

    Level(String type) {
      this.type = type
    }

    String getName() { return type }
  }

  class Builder {
    String date
    String type
    String method
    String fileName
    String message
    String name

    Builder date(String d) {
      this.date = d
      return this
    }

    Builder type(String d) {
      this.type = d
      return this
    }

    Builder method(String method) {
      this.method = method
      return this
    }

    Builder fileName(String fn) {
      this.fileName = fn
      return this
    }

    Builder message(String m) {
      this.message = m
      return this
    }

    Builder name(String n) {
      this.name = n
      return this
    }

    String buildLine() {
      return "[${date}] [${name}] [${type}] [${fileName}] [${method}] ${message}"
    }
  }

  protected FileLogger(String name) {
    this.name = name
  }

  String name
  private static File file = null
  private static LinkedHashMap<String, FileLogger> instance = [:]
  private static final String DEFAULT_EMPTY_VALUE = ""

  static FileLogger getLogger(String name) {
    if (instance.containsKey(name)) return instance.get(name)
    else instance[name] = new FileLogger(name)
    return instance[name]
  }

  protected static File getFile() {
    if (file) return file
    file = new File("./resultprocesor.log")
    if (file.exists()) {
      file.delete()
      file.createNewFile()
    }
    return file
  }

  void log(Level level, String message) {
    def date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
    def msg = new Builder().name(name).date(date).type(level.getName()).message(message).buildLine().replaceAll('null', DEFAULT_EMPTY_VALUE)
    new FileWriter(getFile(), true).withWriter {
      it.write(msg)
      it.append('\n')
      it.flush()
    }
  }

  void error(String msg) {
    log(Level.ERROR, msg)
  }

  void info(String msg) {
    log(Level.INFO, msg)
  }
}
