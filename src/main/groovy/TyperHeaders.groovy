enum TyperHeaders {
  NICK('nick'),
  POINTS('punkty'),
  TOURNAMENT('liga/zawody'),
  DATE('data'),
  SPORT('sport'),
  COL1('Kolumna1'),
  LINK('link'),
  EMPTY('null'),
  ALTER_NICK('Alternatywny nick'),
  NOTES('Uwagi')

  String name

  TyperHeaders(String name) {
    this.name = name
  }

  String getName() {
    return name
  }
}