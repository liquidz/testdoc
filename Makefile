.PHONY: lint test-bb test test-all outdated clean

PWD=$(shell pwd)

lint:
	clj-kondo --lint src:test
	cljstyle check

test-bb:
	docker run --rm -v $(PWD):/tmp -w /tmp uochan/babashka-test 'src' 'test' '_test.clj$$'

test:
	lein test-all

test-all: test test-bb

outdated:
	lein with-profile antq run -m antq.core

clean:
	rm -rf bb
	lein clean
