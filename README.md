# Field Service Manager

En skrivbordsapplikation för ärendehantering inom vitvaruservice, byggd i Java. Projektet sammanfattar grundläggande och objektorienterad programmering med fokus på ren arkitektur, inkapsling, lokal datalagring och automatiserad testning.

## Funktioner
* **Grafiskt användargränssnitt:** Modernt mörkt gränssnitt (*Dark Mode*) byggt med Java Swing och FlatLaf för hantering av arbetsordrar, reservdelar och intäktsöversikt.
* **Arbetsordrar & Garantihantering:** Skapa och klarmarkera serviceärenden där grundavgiften beräknas automatiskt beroende på maskinens garantistatus.
* **Reservdelslager:** Koppla reservdelar till arbetsordrar med automatisk nedräkning av lagersaldo och lagerkontroll.
* **Lokal JSON-lagring:** Alla arbetsordrar och lagersaldon sparas automatiskt till disk (`orders.json` och `inventory.json`) med Gson så att data bevaras mellan körningar.
* **Diagnostik & Intäkter:** Kör maskinspecifik diagnostik och summera totala intäkter över samtliga debiterbara poster.

## Arkitektur & Designval
Projektet följer en tydlig ansvarsfördelning i tre paket:
* `model` – Domänmodeller (`Appliance`, `WashingMachine`, `SparePart`, `ServiceOrder`) samt interfacet `Billable` för polymorf kostnadsberäkning.
* `service` – Affärslogik, JSON-serialisering och datahantering i `DispatchService`, helt frikopplad från användargränssnittet.
* `ui` – Grafiskt gränssnitt (`ServiceManagerGUI`) samt ett alternativt konsolgränssnitt (`ConsoleMenu`).

## Tekniker
* Java 26
* Maven
* Java Swing & FlatLaf (GUI)
* Gson (JSON-persistens)
* JUnit 6 (enhetstester för affärslogik, prisberäkning och undantagshantering)

## Kör projektet
1. Klona repot och öppna projektet i IntelliJ IDEA (eller valfri IDE med Maven-stöd).
2. Kör `src/main/java/se/iths/dario/fieldservicemanager/Main.java` för att starta skrivbordsapplikationen.
3. Kör `DispatchServiceTest.java` under `src/test/java` för att verifiera enhetstesterna.