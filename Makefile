.PHONY: lint test

lint:
	clj-kondo --lint src:test
	cljstyle check

test:
	lein test-all
