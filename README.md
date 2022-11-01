# ResultProcesor
Program sprawdzający, które wyniki z forum zostały już wpisane do arkusza kalk.

# Budowanie
Projekt należy budować z użyciem mavena. Z Lifecycle -> package wychodzą dwie wersje.
Wersja uruchomieniowa to ta z końcówką "-jar-with-dependecies.jar" - jest to paczka zawierająca
wszystkie zależności w jednym.

# Uruchamianie
1. Obecną wersję trzeba uruchamiać z poziomu IntelliJ IDEA, podają argumenty (miesiac, rok) w stosownym miejscu.
2. Na początku jednak należy przygotować plik "TYPER.csv" z danymi i umieścić go w głównym folderze.
3. Format pól w tym pliku jest w przykładowym pliku w katalogu "resources".
4. Po wykonaniu należy w katalogu z plikiem sprawdzić plik "resultprocesor.log"

# Motywacja
Moim celem było napisanie programu, który sprawdzał by spójność naszej bazy.
Oraz użycie języka Groovy, w którym aktualnie pracuję.
