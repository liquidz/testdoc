PWD=$(shell pwd)

.clj-kondo/com.github.liquidz/testdoc/config.edn: resources/clj-kondo.exports/com.github.liquidz/testdoc/config.edn
	@rm -rf .clj-kondo/.cache
	@clj-kondo --copy-configs --dependencies --lint "$(shell clojure -A:dev -Spath)"
.PHONY: update-clj-kondo-config
update-clj-kondo-config: .clj-kondo/com.github.liquidz/testdoc/config.edn

.PHONY: lint
lint: update-clj-kondo-config
	clj-kondo --lint src:test
	cljstyle check

.PHONY: repl
repl:
	iced repl -A:dev

.PHONY: test-bb
test-bb:
	docker run --rm -v $(PWD):/tmp -w /tmp uochan/babashka-test 'src' 'test' '_test.clj$$'

.PHONY: test
test:
	clojure -M:dev:test
	clojure -M:dev:1.9:test

.PHONY: test-all
test-all: test test-bb

.PHONY: outdated
outdated:
	clojure -M:outdated --upgrade

.PHONY: pom
pom:
	clojure -T:build pom

.PHONY: jar
jar:
	clojure -T:build jar

.PHONY: install
install: clean
	clojure -T:build install

.PHONY: deploy
deploy: clean
	clojure -T:build deploy

.PHONY: clean
clean:
	\rm -rf .cpcache target
