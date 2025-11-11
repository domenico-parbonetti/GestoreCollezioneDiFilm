# 🎬 Gestore Film

**Applicazione desktop Java Swing per la gestione di una collezione personale di film con supporto undo e persistenza multi-formato.**

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)
![Swing](https://img.shields.io/badge/UI-Swing-blue?style=flat-square)

---

## 📋 Indice

- [Features](#-features)
- [Design Pattern](#-design-pattern)
- [Utilizzo](#-utilizzo)
- [Autore](#-autore)

---

## ✨ Features

### Gestione Collezione
- ➕ **Aggiungi** film con titolo, regista, anno, genere, valutazione (1-5 stelle), stato visione
- ✏️ **Modifica** film esistenti con validazione input real-time
- 🗑️ **Elimina** film con conferma utente
- 👁️ **Visualizza** collezione in tabella con sorting per colonna

### Undo
- ↩️ **Undo illimitato** fino a 50 operazioni (Ctrl+Z o button)
- 💾 **Stato persistente** anche dopo undo (auto-save immediato)
- 🔄 **Supporto completo** per tutte le operazioni CRUD

### Persistenza Multi-Formato
- 📄 **JSON**: Serializzazione automatica con Gson, formato human-readable
- 📊 **CSV**: Compatibilità Excel/Google Sheets, importabile in Python/R
- 🔄 **Switching runtime**: Cambio formato senza restart applicazione
- 💾 **Auto-save**: Salvataggio automatico dopo ogni operazione

### Ricerca e Filtri
- 🔍 **Ricerca full-text** per titolo o regista
- 🎭 **Filtro genere**: Drama, Action, Sci-Fi, Horror, Comedy, Romance, etc.
- 📺 **Filtro stato**: Da vedere / In visione / Visto
- ⭐ **Filtro rating**: Minimum 1-5 stelle
- 🧹 **Clear filters**: Reset rapido con un click

### User Experience
- 💾 **Auto-save trasparente**: Zero "Vuoi salvare?" dialogs
- ⚡ **Recovery automatico**: Ripristino ultimo stato al riavvio
- ✅ **Validazione input**: Anno 1888-2030, rating 1-5, campi obbligatori
- 💬 **Feedback immediato**: Messaggi successo/errore dopo operazioni
- 🎨 **UI responsive**: Layout adattivo, componenti Swing standard

---

## 🏗️ Design Pattern

Il progetto implementa **5 Design Pattern GoF** per garantire manutenibilità, estendibilità e robustezza:

### 1️⃣ **Singleton** (Creational)
- **Dove**: `MovieCollection`
- **Perché**: Garantire unica istanza collezione, prevenire inconsistenze
- **Implementazione**: `getInstance()` sincronizzato per thread-safety

### 2️⃣ **Strategy** (Behavioral)
- **Dove**: `persistence` package
- **Perché**: Supportare formati multipli (JSON/CSV), switching runtime
- **Implementazione**: `PersistenceStrategy` interface + `JSONPersistence` / `CSVPersistence`

### 3️⃣ **Command** (Behavioral)
- **Dove**: `commands` package
- **Perché**: Incapsulare operazioni CRUD, abilitare undo/redo
- **Implementazione**: `Command` interface + `AddMovieCommand` / `EditMovieCommand` / `DeleteMovieCommand`

### 4️⃣ **Memento** (Behavioral)
- **Dove**: `Movie.Memento` inner class
- **Perché**: Salvare/ripristinare stato film per undo senza violare encapsulation
- **Implementazione**: Snapshot immutabile con campi final, costruttore privato

### 5️⃣ **MVC** (Architectural)
- **Dove**: Architettura generale
- **Perché**: Separare presentazione (View), business logic (Model), coordinamento (Controller)
- **Implementazione**: `view` package (Swing UI) + `model` package (business) + `MovieController`

---

## 🎮 Utilizzo

### Operazioni Principali

#### ➕ Aggiungere un Film
1. Click button **"Aggiungi Film"**
2. Compila form (titolo, regista, anno, genere, rating, stato)
3. Click **"Salva"**
4. Film aggiunto e auto-save automatico

#### ✏️ Modificare un Film
1. Seleziona film dalla tabella
2. Click button **"Modifica"**
3. Modifica campi desiderati
4. Click **"Salva"**
5. Modifiche applicate e auto-save automatico

#### 🗑️ Eliminare un Film
1. Seleziona film dalla tabella
2. Click button **"Elimina"**
3. Conferma nel dialog
4. Film rimosso e auto-save automatico

#### ↩️ Annullare Operazione (Undo)
- **Metodo 1**: Click button **"Undo"**
- **Metodo 2**: Shortcut **Ctrl+Z**
- **Limite**: Ultime 50 operazioni

#### 🔍 Ricerca e Filtri
1. **Search bar**: Digita titolo o nome regista
2. **Genere dropdown**: Seleziona genere specifico
3. **Stato dropdown**: Filtra per Da vedere / In visione / Visto
4. **Rating dropdown**: Filtra per valutazione minima (1-5 stelle)
5. **Clear Filters**: Reset tutti i filtri

#### 💾 Salvataggio e Caricamento
**Auto-save**: Attivo automaticamente, nessuna azione richiesta

**Salvataggio Manuale**:
1. Seleziona formato (JSON/CSV) da dropdown
2. Click **"Salva"**
3. Scegli percorso e nome file
4. Collezione salvata nel formato scelto

**Caricamento**:
1. Click **"Carica"**
2. Seleziona file JSON o CSV
3. Collezione sostituita con file caricato

---

## 👤 Autore

**Domenico Parbonetti**  
🎓 Corso: Ingegneria del Software  
🏫 Università: Università della Calabria 
📅 Anno Accademico: 2024-2025
