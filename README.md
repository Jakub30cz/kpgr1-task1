# KPGR1 – Úloha 1: rasterizace, vyplňování a ořezávání

Autor: Jakub Jüthner

Interaktivní Swing aplikace, která demonstruje základní rasterové algoritmy počítačové grafiky:
kreslení úseček, vyplňování oblastí (semínkové a řádkové) a ořezávání úseček a polygonů.
Veškerý obsah scény se zapisuje do rastru pixel po pixelu přes `Raster.setPixel`;
`Graphics2D.drawLine` ani `fillPolygon` se pro obsah scény nepoužívají.

Aplikace používá Javu 25, čisté Swing a žádné externí knihovny (testy používají JUnit 5).

## Překlad a spuštění

```bash
./run-tests.sh                    # přeloží zdrojové kódy a testy do bin/ a spustí JUnit 5 testy
java -cp bin/main kpgr1.App       # spuštění aplikace (po předchozím ./run-tests.sh)
```

Alternativně `make test` (testy) a `make run` (spuštění přes `run.sh`).
Konkrétní testovací třídu lze spustit takto: `./run-tests.sh --select-class kpgr1.model.PolygonTest`.

## Ovládání

Aktivní režim se přepíná klávesami; nápověda k aktivnímu režimu je ve stavovém řádku (`StatusBar`).

| Vstup | Akce |
|---|---|
| `L` | režim úsečky (stisk = začátek, tažení = gumová úsečka, uvolnění = potvrzení; držení Shift = přichycení k 0/45/90°) |
| `P` | režim polygonu (levý klik/tažení přidá vrchol do aktuálního polygonu; `Enter` jej dokončí a začne nový) |
| `F` | režim semínkového vyplnění (levý klik = semínko) |
| `B` | přepnutí podmínky semínkového vyplnění: zastavit na barvě hranice / vyplňovat jen barvu pozadí |
| `T` | přepnutí stylu výplně: plná barva / vzorek (platí pro semínkové i řádkové vyplnění) |
| `S` | režim řádkového vyplnění (levý klik uvnitř polygonu jej vyplní; vyhrává nejvyšší obsahující polygon) |
| `R` | režim ořezávacího obdélníku (stisk-tažení-uvolnění určí okno pro Cohen-Sutherlanda) |
| `K` | režim ořezávacího polygonu (pokud žádný neexistuje, vytvoří se výchozí pravidelný pětiúhelník; `C` jej smaže a znovu se vytvoří až klávesou `K` nebo prvním klikem v tomto režimu) |
| `O` | přepnutí orientace ořezávacího polygonu CW/CCW (obrátí pořadí jeho vrcholů) |
| pravé tlačítko + tažení | posun nejbližšího vrcholu (v každém režimu, poloměr výběru 10 px) |
| Ctrl + levý klik | vložení vrcholu na nejbližší hranu polygonu / ořezávacího polygonu |
| `Delete` / `Backspace` | smazání vrcholu polygonu nejbližšího kurzoru (polygon s méně než 3 vrcholy se odstraní; ořezávací polygon si ponechá alespoň 5) |
| `X` | smazání celého polygonu nejbližšího kurzoru |
| `C` | vymazání plátna, scény i stavu všech nástrojů |

## Algoritmy a třídy, které je implementují

| Algoritmus | Třída | Popis |
|---|---|---|
| Bresenhamův algoritmus | `BresenhamLineRasterizer` (potomek `LineRasterizer`) | celočíselné vykreslení úsečky ve všech osmi oktantech pomocí rozhodovací proměnné; bez desetinných čísel |
| Přichycení úsečky (Shift) | `LineSnap` | koncový bod se přichytí k jednomu z osmi směrů (vodorovně, svisle, úhlopříčně) |
| Semínkové vyplnění | `SeedFiller` | 4-souvislé, s explicitní frontou (`ArrayDeque`, bez rekurze); dvě podmínky (`SeedFillMode`): zastavit na barvě hranice (`STOP_ON_BOUNDARY`) nebo vyplňovat jen barvu pozadí (`FILL_BACKGROUND_ONLY`); barvu určuje `Pattern` |
| Vzorek výplně | `Pattern`, `SolidPattern`, `MatrixPattern` | plná barva nebo matice barev (např. šachovnice) opakovaná přes `x % w`, `y % h` |
| Řádkové vyplnění (scan-line) | `ScanLineFiller`, `Edge`, `IntSort` | kroky: sestavení hran, vyřazení vodorovných, orientace shora dolů (`Edge`), zkrácení o 1 px (`y >= y1 && y < y2`), určení yMin/yMax, průsečíky na každém řádku, vlastní seřazení (`IntSort.insertionSort`), vyplnění dvojic vzorkem a nakonec překreslení obrysu |
| Cohen-Sutherlandův ořez úseček | `CohenSutherlandClipper` | výpočet outcode, triviální přijetí/zamítnutí, jinak průsečík s hranou okna |
| Sutherland-Hodgmanův ořez polygonů | `SutherlandHodgmanClipper`, `PolygonGeometry` | ořez hranu po hraně; test „uvnitř“ respektuje orientaci ořezávacího polygonu, takže funguje pro CW i CCW; `PolygonGeometry` počítá orientaci (`signedArea`, `isClockwise`) a konvexnost (`isConvex`) |

## Struktura balíčků

```
src/kpgr1/
  App                  vstupní bod (main)
  raster/              Raster, ImageRaster (BufferedImage), ArrayRaster (int[] pro testy)
  model/               Point, Line, Polygon, ClipRectangle, Scene, SeedFillCommand,
                       VertexHandle, EdgeHit, Picking
  rasterize/           LineRasterizer, BresenhamLineRasterizer, LineSnap
  fill/                Filler, Pattern, SolidPattern, MatrixPattern, SeedFillMode,
                       SeedFiller, ScanLineFiller, Edge, IntSort
  clip/                CohenSutherlandClipper, SutherlandHodgmanClipper, PolygonGeometry
  render/              Palette, SceneRenderer
  tool/                Tool, LineTool, PolygonTool, ClipRectangleTool, ClipPolygonTool,
                       SeedFillTool, ScanLineTool, FillSettings, VertexEditor
  ui/                  MainFrame, CanvasPanel, StatusBar
test/kpgr1/...         JUnit 5 testy odpovídající balíčkům výše
```

Architektura: scéna (`Scene`) je uchovávaný model a při každé události myši se celá překreslí
(`SceneRenderer.render`) do rastru. Semínková vyplnění se ukládají jako `SeedFillCommand` a přehrávají se
po vykreslení veškeré geometrie. Výsledky ořezu se počítají při každém překreslení (nejsou ukládány);
původní objekty se při ořezu kreslí šedě a výsledek barevně. Každý režim je strategie `Tool`;
posun vrcholů pravým tlačítkem je společný pro všechny režimy (`VertexEditor` + `Picking`).

## Implementované bonusy

- vyplnění vzorkem (semínkové i řádkové),
- semínkové vyplnění s explicitní frontou (bez rekurze),
- úprava koncových bodů úseček,
- úprava a mazání polygonů a jejich vrcholů,
- vkládání vrcholu na nejbližší hranu polygonu,
- libovolný počet úseček a polygonů ve scéně, přichycení úsečky klávesou Shift,
- ořezávací polygon v orientaci CW i CCW s přepínáním.

## Použití AI

Použití AI uvádím v souladu s pokynem prorektora UHK č. 7/2026 (účel, rozsah, ověření).

- **Nástroj:** Claude Code (Anthropic) včetně jeho AI agentů.
- **Rozsah:** AI napsala většinu zdrojového kódu v `src/`, automatické testy v `test/`, tento README
  a předvyplnila hodnoticí tabulku v `doc/`.
- **Způsob:** zadal jsem požadavky podle zadání úlohy a hodnoticí tabulky a rozhodl o algoritmech,
  architektuře, ovládání a rozsahu bonusů. AI implementovala řešení po malých krocích metodou TDD
  (nejdřív test, pak implementace) s commitem po každém kroku.
- **Ověření:** 120 testů JUnit 5 (`./run-tests.sh`), ruční vyzkoušení všech režimů v aplikaci
  a kontrola hodnoticí tabulky proti programu. Za odevzdané řešení odpovídám já.
