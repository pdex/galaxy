# galaxy

FIXME: write description

## Usage

FIXME: this will work for the time being:

```
$ lein repl
REPL started; server listening on localhost port 11714
user=> (use '(galaxy demo render))
nil
user=> (let [animation (build-animation (build-frame (build-render demo)))] (send-off animator animation))
```

## Installation

FIXME: write

## License

Copyright (C) 2010 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
