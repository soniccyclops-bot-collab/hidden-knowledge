# Hidden Knowledge

A full-stack Clojure application for discovering overlooked historical connections and niche knowledge through pattern recognition and document analysis.

## Features

- **Internet Archive Integration**: Direct search and analysis of historical documents
- **Niche Knowledge Discovery**: Find overlooked sources that mainstream academia missed
- **Entity Extraction**: Automatically identify names, places, dates, and relationships
- **Pattern Recognition**: Correlate events across time periods
- **Primary Source Focus**: Emphasizes archival documents from 1600-1950

## Tech Stack

- **Backend**: Clojure + Ring + Compojure
- **Frontend**: ClojureScript + Reagent + Re-frame
- **Data Sources**: Internet Archive, historical document collections
- **Analysis**: NLP, entity extraction, timeline correlation

## Quick Start

```bash
# Setup
make setup

# Development mode (auto-rebuild + server)
make dev

# Access at http://localhost:3000
```

## Example Research Queries

### Magnetism Origins
**Query**: "How did they magnetize the first magnets?"
**Expected Sources**: British Royal Society papers from 1600s-1700s on lodestone experiments

### Early Banking
**Query**: "What were the banking innovations in 1600s Amsterdam?"
**Expected Sources**: Dutch East India Company documents, merchant guild records

### Forgotten Inventors
**Query**: "Who invented the first mechanical calculating devices?"
**Expected Sources**: 17th century mechanical engineering texts, patent records

### Agricultural Techniques
**Query**: "How did medieval farmers increase crop yields?"
**Expected Sources**: Agricultural treatises, monastery records, farming manuals

## API Endpoints

### Research Query
```bash
POST /api/research
{
  "question": "How did they magnetize the first magnets?"
}
```

Returns:
- Extracted keywords
- Relevant Internet Archive sources
- Historical document matches
- Timeline context

### Document Analysis
```bash
POST /api/analyze
{
  "identifier": "britishroyal00royagoog"
}
```

Returns:
- Extracted entities (names, places, dates)
- Key terminology frequency
- Pattern recognition results
- Source verification data

### Search Interface
```bash
GET /api/search?q=magnetism&year-start=1600&year-end=1800
```

## Development

```bash
# Start backend only
make server

# Build frontend only
make build

# Watch mode for frontend
make watch

# Test research functionality
make test-query

# Clean build artifacts
make clean
```

## Architecture

```
Frontend (ClojureScript + React)
├── Research Input Interface
├── Keyword Extraction Display
├── Source Results Grid
└── Document Analysis Panel

Backend (Clojure + Ring)
├── Internet Archive Client
├── Document Text Extraction
├── Entity Recognition Engine
├── Pattern Analysis Tools
└── Research Query Processor
```

## Data Sources

- **Internet Archive**: Primary historical document repository
- **Collections Focus**: 
  - texts (books, papers, documents)
  - Historical date range: 1600-1950
  - Scientific papers, government documents, trade records
- **Document Types**: OCR'd texts, scanned manuscripts, published works

## Research Methodology

1. **Query Processing**: Extract key terms and historical context
2. **Source Discovery**: Search multiple document collections
3. **Relevance Ranking**: Prioritize primary sources and contemporary accounts
4. **Entity Extraction**: Identify people, places, events, and relationships
5. **Timeline Correlation**: Map cause-and-effect across time periods
6. **Source Verification**: Cross-reference multiple documents

## Target Use Cases

- Academic historians researching overlooked topics
- Journalists investigating historical claims
- Genealogists tracing family connections
- Patent researchers finding prior art
- Anyone curious about "how did they..." questions

The goal is to democratize access to historical primary sources and make niche knowledge discoverable through intelligent search and analysis.

## License

MIT