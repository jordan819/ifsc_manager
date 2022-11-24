# Aplikacja do zarządzania zawodnikami międzynarodowej federacji wspinaczki sportowej (IFSC)
## Opis aplikacji
Głównymi zadaniami aplikacji będzie pobieranie i przechowywanie danych o zawodnikach wspinaczki sportowej, prezentowanie ich w przystępnej formie,
oraz umożliwienie analizy wyników. Program będzie pozwalał także na wprowadzenie informacji o nowych zawodnikach, czy też edycję już istniejących.
Kierowany jest do trenerów wspinaczki sportowej, którzy będą mogli podejmować trafniejsze decyzje względem swoich podopiecznych.

## Funkcjonalności

### Pobieranie danych z oficjalnych zawodów
Aplikacja będzie umożliwiała użytkownikowi pobranie uczestników i ich osiągnięć z ogólnodostępnej [strony internetowej](www.ifsc-climbing.org).
Ze względu na brak udostępnianego API, wykorzystana zostanie technika web scrapingu.

### Ręczne wprowadzanie zawodników
Użytkownik będzie miał możliwość ręcznego wprowadzenia zawodników, wraz ze wszystkimi informacjami o nich: imię i nazwisko, data urodzenia,
wyniki w poszczególnych zawodach. Wszystkie dane będą mogły zostać później zmienione, lub usunięte.

### Zapisywanie danych w lokalnej bazie
Dane będą zapisywane w lokalnej bazie danych, umożliwiając ich odczyt i edycję w dowolnym momencie. Konieczna będzie implementacja mechanizmu zapobiegającego
przypadkowemu nadpisaniu i utracie danych, na rzecz nowo pobieranych rekordów.

### Import i eksport danych
Użytkownik będzie mógł wyeksportować dane z aplikacji do pliku CSV, a także je z niego wczytać. Umożliwi to tworzenie kopii zapasowych nazewnętrznych nośnikach danych,
a także udostępnianie ich pomiędzy urządzeniami.

### Wyświetlanie i analiza wyników
Na podstawie wprowadzonych danych, aplikacja będzie generowała tabele i wykresy, przedstawiające rozwój zawodnika w czasie, lub jego osiągnięcia na tle innych uczestników.

## Model bazy danych
[![Diagram-ERD.png](https://i.postimg.cc/RFYt3xwG/Diagram-ERD.png)](https://postimg.cc/XGwqhhHy)
