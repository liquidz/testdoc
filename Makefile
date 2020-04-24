.PHONY: lint test

PWD=$(shell pwd)

lint:
	clj-kondo --lint src:test
	cljstyle check

bb:
	curl -s https://raw.githubusercontent.com/borkdude/babashka/master/install -o .install-babashka
	bash ./.install-babashka $(PWD)

test-bb: bb
	./bb --classpath "src:test" -m bb-test-runner

test:
	lein test-all

test-all: test test-bb

clean:
	rm -rf bb
	lein clean
