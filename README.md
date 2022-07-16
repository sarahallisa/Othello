# Othello
Projekt als Prüfungsleistung der Klausur von dem "**Programmierung interaktiver Systeme**"-Modul.

## Einführung zum Spiel
**Othello** ist ein strategisches Brettspiel für zwei Personen. Ein Spieler muss seinen Stein auf ein leeres Feld lege, das horizontal, vertikal oder diagonal an ein bereits belegtes Feld angrenzt. Wird ein Stein gelegt, werden alle gegnerischen Steine, die sich zwischen dem neuen Spielstein und einem bereits gelegten Stein der eigenen Farbe befinden, umgedreht. Wenn es für keinen der Spieler mehr möglich ist, einen Zug zu machen, ist das Spiel vorbei. Nun werden die Spielsteins gezählt und der Spieler, der die meisten seiner Farbsstein auf dem Brett hat, ist Sieger. Ein Unentschieden ist möglich.

Die gesamte Datei werden auf zwei geteilt; einer als der Teil des Anwendungslogiks und der andere als Teil des Interaktionslogiks.

## Anwendungslogik
Bei der Anwendungslogik sind eine *Othello*-Klasse, *Move*-Klasse und ein Interface erstellt. In der Klasse *Othello* ist alles Notwendige zum Ausführen des Spiels implementiert. Die *Move*-Klasse hilft die Beschreibung der Züge, die in der *Othello*-Klasse benutzt werden. 

Die Klasse hat insgesamt 3 **int**-Attribute:
* eine zur Bestimmung der Reihe
* eine zur Bestimmung der Spalte
* eine zum Markieren des Spielers, der den Zug macht

## Interaktionslogik
Die graphisch-orientierte Interaktionslogik ist mit ***Processing*** realisiert und über das *Othello*-Interface auf die Anwendungslogik zugegriffen. Beim Starten werden die Benutzer aufgefordert, zu wählen, ob sie gegen eine AI (**pvc** = *player vs. computer*) oder einen anderen Spieler (**pvp** = *player vs. player*) spielen möchten. Nach der Auswahl dürfen Benutzer das Spiel spielen.

In der oberen linken Ecke des laufenden Spiels befindet sich eine Kennung, damit sie wissen, wer gerade spielt. Der Spieler, der dran ist, darf nur eines der hellgrünen Kästchen auswählen, um einen Zug zu machen. Im Laufe des Spiels wird die Gesamtpunktzahl jedes Spielers immer aktualisiert. Außerdem haben Benutzer die Möglichkeit, das Spiel neu zu starten, einen Zug rückgängig zu machen und zum Hauptmenü zurücksukehren.

## AI
Zur Bestimmung den besten Zug für das AI wird der ***minimax***-Algorithmud umgesetzt. Bei Minimax heißen die beiden Spieler Maximierer und Minimierer. Der Maximierer versucht, die höchstmögliche Punktzahl zu erzielen, während der Minimierer versucht, das Gegenteil zu tun und die niedrigstmögliche Punktzahl zu erzielen.

Um den Spielbaum in sinnvoller Zeit durchrechnen zu können wird die ***Monte-Carlo-Tief-Suche***-Methode eingesetzt. Diese Methode ist ein Verahren, der eine gegebene Spielstellung bewertet.

```simulatePlays()``` lässt das Spiel 100 Mal mit zufälligen Zügen als Simulation spielen.\
```evaluateMoves()``` wird 100 beim Gewinnen, -100 beim Verloren und 0 beim Unentschieden als Bewertung zurückgegeben.\
Die Bewertung wird durch diese Formel berechnet werden: ```Gewinnen - Verloren```.

Wenn der gewünschte Tiefe bei der ***minimax***-Algorithmus erreicht ist, wird die beste Bewertung von den möglichen Zügen genommen.





