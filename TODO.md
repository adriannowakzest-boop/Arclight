# PaperBridge 1.20.1 — TODO

Cel: rozwijać Arclight dla Minecraft 1.20.1 tak, aby na jednym serwerze działały mody Forge oraz możliwie szeroki zestaw pluginów Bukkit/Spigot/Paper.

> **Ważne:** zgodność ze wszystkimi pluginami Paper nie jest realistyczną obietnicą. Każdy plugin może używać innego zestawu API, NMS, zależności i założeń dotyczących implementacji Paper. Zgodność będzie potwierdzana testami i opisywana w macierzy kompatybilności.

## 0. Bieżąca baza

- [x] Utworzyć gałąź `paperbridge-1.20.1` na bazie linii Arclight Trials dla Minecraft 1.20.1.
- [x] Ustawić Minecraft na `1.20.1`.
- [x] Ustawić Forge na `47.4.29`.
- [x] Ujednolicić wersję SpongePowered Mixin na `0.8.5` w konfiguracji projektu.
- [x] Ustawić procesor adnotacji Mixin w module wspólnym.
- [x] Dodać repozytorium Maven PaperMC.
- [x] Dodać zależność Paper API `1.20.1-R0.1-SNAPSHOT` do pakowania modułu wspólnego.
- [x] Dodać osobny workflow GitHub Actions dla gałęzi PaperBridge.
- [x] Opisać aktualny zakres i ograniczenia projektu w README.

## 1. Stabilny build i uruchamianie

- [x] Uruchomić `./gradlew cleanBuild remapSpigotJar idea --no-daemon --stacktrace` na czystym środowisku Java 17 (PaperBridge CI, Java 17).
- [x] Uruchomić `./gradlew build collect --no-daemon --stacktrace` i naprawić wszystkie błędy kompilacji (PaperBridge CI, Forge 47.4.29).
- [ ] Potwierdzić, że Forge `1.20.1-47.4.29` rozwiązuje się z Maven i uruchamia serwer.
- [ ] Potwierdzić, że Paper API `1.20.1-R0.1-SNAPSHOT` rozwiązuje się poprawnie i nie powoduje konfliktów zależności ze Spigot/Bukkit.
- [ ] Sprawdzić, czy pakowanie API do JAR-a nie duplikuje klas lub bibliotek dostarczanych przez Forge/Arclight.
- [ ] Zweryfikować generowanie `mixins.arclight.refmap.json` oraz jego obecność w artefakcie.
- [ ] Zweryfikować manifest `MixinConnector`, ładowanie konfiguracji Mixin i kolejność inicjalizacji.
- [ ] Uruchomić serwer testowy z czystym katalogiem świata i potwierdzić poprawny start, zatrzymanie oraz ponowny start.
- [ ] Zachować logi builda i startu jako artefakty CI.

## 2. Runtime Paper

- [x] Wybrać i przypiąć historyczne źródła Paper zgodne z Minecraft 1.20.1: Paper build 196, commit `773dd724469bae89d0c2075edc3d1ddc8d5b0b18`.
- [ ] Zweryfikować w CI skrypt odtwarzający źródła i nakładający patche Paper (`scripts/prepare-paper-upstream.sh`).
- [ ] Przygotować proces portowania patchy Paper na kod Arclight/Forge; samo odtworzenie drzewa Paper nie jest integracją runtime.
- [ ] Przenieść wymagane poprawki serwera Paper bez nadpisywania istniejących poprawek Arclight.
- [x] Przenieść bezkolizyjne klasy identyfikacyjne `com.destroystokyo.paper.PaperConfig` i `PaperWorldConfig` z patcha Paper `0005-Paper-config-files`.
- [ ] Przenieść właściwy system konfiguracji Paper (Configurate, konfiguracja globalna/światów i integracja z cyklem życia); same klasy identyfikacyjne nie implementują konfiguracji.
- [ ] Zaimplementować brakujące klasy i zachowania Paper API wymagane w runtime — samo dołączenie pliku Paper API do JAR-a nie wystarcza.
- [ ] Uzupełnić Paper-specific zachowania świata, chunków, encji, ticków i konfiguracji.
- [ ] Uzupełnić API zdarzeń Paper i sprawdzić kolejność wywołań względem zdarzeń Bukkit oraz Forge.
- [ ] Dodać testy zgodności API: klasy, metody, pola, wartości domyślne i zachowanie w runtime.

## 3. Ładowanie pluginów

- [ ] Zinwentaryzować istniejący loader Bukkit/Spigot w Arclight i określić, co można wykorzystać bez duplikowania.
- [ ] Zweryfikować ładowanie pluginów z katalogu `plugins/` oraz obsługę `plugin.yml`.
- [ ] Zweryfikować zależności `depend`, `softdepend`, `loadbefore` i fazy ładowania.
- [ ] Sprawdzić izolację classloaderów, kolejność classpath i rozwiązywanie zależności pluginów.
- [ ] Sprawdzić rejestrację i wyrejestrowywanie komend, eventów, listenerów i schedulerów.
- [x] Dodać walidację typów pól deskryptora Paper, poprawności nazwy klasy `main`, fazy ładowania oraz list `authors`, `provides` i `libraries`.
- [ ] Dodać czytelne komunikaty o brakujących zależnościach, błędach inicjalizacji i niezgodnych wersjach API.
- [ ] Zaimplementować obsługę `paper-plugin.yml`, jeżeli format jest wspierany przez wybraną wersję Paper 1.20.1; nie zakładać zgodności z późniejszymi wersjami specyfikacji.
- [x] Przekazywać `libraries` z `paper-plugin.yml` do Paper `PluginDescriptionFile` i istniejącego `LibraryLoader` (Maven GAV).
- [ ] Zaimplementować natywny Paper loader i zależności między pluginami.
- [x] Dodać eksperymentalny fallback `paper-plugin.yml` → `PluginDescriptionFile` dla prostych pluginów JavaPlugin.
- [x] Zachowywać deklaracje komend (`commands`) z `paper-plugin.yml` w deskryptorze Bukkit, aby legacy loader rejestrował je tak jak w `plugin.yml`.
- [x] Mapować podstawowe `dependencies.server` na `depend`, `softdepend` i `loadbefore`.
- [x] Odrzucać nieobsługiwane bootstrappery, custom loadery, `join-classpath: false` i konfliktowe zależności zamiast ignorować ich semantykę.
- [x] Walidować strukturę `dependencies`, obsługiwane zakresy oraz typy pól `required`, `join-classpath` i `load`; odrzucać błędne deskryptory zamiast po cichu zmieniać ich znaczenie.
- [x] Walidować typy podstawowych metadanych, nazwę klasy `main`, fazę ładowania oraz listy `authors`, `provides`, `libraries`; odrzucać nieznane opcje zależności serwera.
- [x] Dodawać ścieżkę JAR-a do błędów odczytu deskryptora oraz nazwę JAR-a do błędów ładowania klas pluginu.
- [ ] Potwierdzić testami i buildem, że fallback działa na Forge 47.4.29.
- [ ] Sprawdzić bezpieczne wyłączanie pluginu podczas zatrzymania serwera.

## 4. Integracja Forge

Istniejące mosty Forge → Bukkit w kodzie obejmują event bus, obsługę zdarzeń bloków, encji i świata, kanały sieciowe, dystrybucję pakietów oraz przekazywanie uprawnień. Ich obecność nie zastępuje testów runtime z modami.

- [x] Zarejestrować dispatchery Forge dla zdarzeń niszczenia/stawiania bloków, deptania grządek, śmierci i oswajania encji, teleportacji, efektów mikstur, despawnu przedmiotów i usuwania światów.
- [x] Dodać most Forge Permission API → Bukkit permissions (konfigurowalny).
- [x] Zachować domyślny handler Forge dla węzłów, które nie są zarejestrowane jako uprawnienia Bukkit.
- [x] Uwzględnić kanały plugin messaging Bukkit w rejestracji kanałów Forge.
- [x] Zabezpieczyć dystrybucję pakietów Forge do graczy przed wysyłką przez niegotowe połączenie.
- [x] Czyścić przechwycony kierunek stawiania bloku, gdy Forge nie opublikuje zdarzenia placement.
- [x] Anulować teleportację Forge, gdy plugin ustawi docelową lokalizację Bukkit na null.
- [x] Odrzucać nieprawidłowe cele teleportacji (inny świat, null lub współrzędne NaN/Infinity).
- [x] Zachowywać wcześniejsze anulowanie Forge podczas przekazywania teleportacji, despawnu przedmiotów i usuwania efektów mikstur do Bukkit.
- [x] Zachowywać wcześniejsze anulowanie Forge w zdarzeniach niszczenia i stawiania bloków po wywołaniu listenerów Bukkit.
- [x] Zachowywać wcześniejsze anulowanie Forge podczas przekazywania `AnimalTameEvent` do Bukkit.
- [x] Przekazywać Forge `MobSpawnEvent.FinalizeSpawn` do Bukkit `CreatureSpawnEvent` z mapowaniem przyczyny spawnu i obsługą anulowania przed finalizacją moba.
- [x] Przekazywać dodawanie `ItemEntity` do serwerowego świata przez Forge do Bukkit `ItemSpawnEvent`; anulowanie blokuje dodanie przedmiotu.
- [x] Jawnie mapować wszystkie 16 wartości `MobSpawnType` z Minecraft 1.20.1 na odpowiadające im `CreatureSpawnEvent.SpawnReason` lub `DEFAULT`.
- [ ] Zweryfikować semantykę spawnów niestandardowych modów oraz przypisanie im przyczyn Bukkit.
- [x] Wywoływać Bukkit `WorldUnloadEvent` przed usunięciem świata przez Forge; respektować anulowanie unloadu.
- [ ] Zmapować i zweryfikować cykl życia serwera, świata, gracza, encji i chunków pomiędzy Forge oraz Bukkit/Paper.
- [x] Zabezpieczyć callbacki unload chunków i światów, gdy Bukkit Server nie jest już dostępny podczas zamykania.
- [x] Kontynuować kolejkę callbacków serwera po błędzie RuntimeException w pojedynczym callbacku, aby nie blokować kolejnych zdarzeń chunków.
- [ ] Ustalić i przetestować jednoznaczną kolejność wywołań eventów Forge i Bukkit/Paper.
- [ ] Przetestować bloki, przedmioty, encje i wymiary dodane przez mody.
- [ ] Przetestować capability Forge oraz dostęp pluginów do obiektów modowanych.
- [ ] Zweryfikować zachowanie ekwipunków, interakcji, obrażeń, spawnu, śmierci i teleportacji.
- [ ] Sprawdzić synchronizację chunków, tickowanie i granice wątków.
- [ ] Dodać ochronę przed podwójnym wywoływaniem eventów i niespójnym stanem świata.
- [ ] Przygotować zestaw testowych modów Forge do regresji.

## 5. Testy pluginów i zgodność

- [ ] Przygotować automatyczny test instalowania, wczytywania i wyłączania pluginów.
- [ ] Przetestować plugin używający wyłącznie Bukkit API.
- [ ] Przetestować plugin używający Spigot API.
- [ ] Przetestować plugin używający Paper API.
- [ ] Przetestować plugin korzystający z NMS i opisać ograniczenia wersji.
- [ ] Przetestować EssentialsX.
- [ ] Przetestować LuckPerms.
- [ ] Przetestować WorldEdit.
- [ ] Przetestować WorldGuard.
- [ ] Przetestować pluginy z zależnościami Maven i własnymi bibliotekami.
- [ ] Utworzyć macierz kompatybilności: działa / działa częściowo / nie działa / nieprzetestowany.
- [ ] Testować pluginy razem z reprezentatywnym zestawem modów Forge, nie tylko na czystym serwerze.

## 6. Wydanie i dokumentacja

- [ ] Dodać testy regresyjne uruchamiane przy każdym pushu do `paperbridge-1.20.1`.
- [ ] Dodać walidację plików Mixin i refmap podczas CI.
- [ ] Publikować tylko artefakty, które przeszły build i test startu.
- [ ] Opisać instalację serwera, modów Forge i pluginów.
- [ ] Udokumentować różnice między `plugin.yml` i `paper-plugin.yml`.
- [ ] Udokumentować API i funkcje Paper, które nie są jeszcze obsługiwane.
- [ ] Przygotować przykładowy katalog `plugins/` i konfigurację testową.
- [ ] Oznaczyć wydanie jako eksperymentalne do czasu przejścia testów kompatybilności.

## Kryterium ukończenia

PaperBridge 1.20.1 można uznać za gotowy do pierwszego wydania testowego, gdy czysty build przechodzi w CI, serwer uruchamia się na Forge 47.4.29 / Java 17, pluginy Bukkit/Spigot są ładowane, kluczowe elementy Paper API są zaimplementowane i zweryfikowane, a wyniki testów są opublikowane w macierzy kompatybilności.
