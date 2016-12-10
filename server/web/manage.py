#!/usr/bin/env python

import subprocess

import click


@click.group()
def cli():
    """This is a management script for the flask application."""


@cli.command('run', short_help='Runs a development server.')
@click.option('--host', '-h', default='127.0.0.1',
              help='The interface to bind to.')
@click.option('--port', '-p', default=5000,
              help='The port to bind to.')
@click.option('--reload/--no-reload', default=None,
              help='Enable or disable the reloader.  By default the reloader '
              'is active if debug is enabled.')
@click.option('--debugger/--no-debugger', default=None,
              help='Enable or disable the debugger.  By default the debugger '
              'is active if debug is enabled.')
def runserver(host, port, reload, debugger):
    """Runs a local development server for the Flask application.
    This local server is recommended for development purposes only but it
    can also be used for simple intranet deployments.  By default it will
    not support any sort of concurrency at all to simplify debugging.
    The reloader and debugger are by default enabled if the debug flag of
    Flask is enabled and disabled otherwise.
    """

    from app import create_app
    app = create_app()

    if reload is None:
        reload = app.debug
    if debugger is None:
        debugger = app.debug

    app.run(host, port, use_reloader=reload, use_debugger=debugger)


@cli.command()
def test():
    # import unittest
    # tests = unittest.TestLoader().discover('tests')
    # unittest.TextTestRunner(verbosity=2).run(tests)
    test_process = subprocess.Popen("pytest", shell=True)
    test_process.wait()


if __name__ == '__main__':
    cli()
