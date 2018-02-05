# leaflike

[![CircleCI](https://circleci.com/gh/nilenso/leaflike/tree/master.svg?style=svg)](https://circleci.com/gh/nilenso/leaflike/tree/master)

A Clojure library designed to manage bookmarks.

## Setup and Installation

### Development

Install Java 8, leiningen and Postgresql >=9.6.6. 

#### Clone repository

```bash
$ git clone https://github.com/nilenso/leaflike.git
```

#### Setup Databases

```
$ createdb -U <username> leaflike
$ createdb -U <username> leaflike_test
```

**Note : Change postgres username and password in `config.edn.test` and `config.edn.dev` if required**

#### Run Tests

```bash
$ chmod +x scripts/run_tests.sh
$ ./scripts/run_tests.sh
```

#### Run app

```
$ cp resources/config/config.edn.dev resources/config/config.edn
$ lein run migrate
$ lein run
```

### Production

```bash
$ git clone https://github.com/nilenso/leaflike.git
$ ./scripts/setup.sh
$ ./scripts/deploy.sh
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
