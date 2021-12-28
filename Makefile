.PHONY: lint test-bb test test-all outdated clean

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

test-bb:
	docker run --rm -v $(PWD):/tmp -w /tmp uochan/babashka-test 'src' 'test' '_test.clj$$'

test:
	lein test-all

test-all: test test-bb

outdated:
	lein with-profile antq run -m antq.core --upgrade

clean:
	rm -rf bb
	lein clean
