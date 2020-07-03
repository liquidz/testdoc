.PHONY: lint test

PWD=$(shell pwd)

lint:
	clj-kondo --lint src:test
	cljstyle check

test-bb:
	docker run --rm -v $(PWD):/tmp -w /tmp uochan/babashka-test 'src' 'test' '_test.clj$$'

test:
	lein test-all

test-all: test test-bb

clean:
	rm -rf bb
	lein clean
