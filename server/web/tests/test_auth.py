from unittest import mock

import pytest

from app import create_app, db
from app.models import User


@pytest.fixture(scope="module")
def app():
    app = create_app("testing")

    with app.app_context():
        yield app


@pytest.fixture()
def prepare_one_user(app):
    db.create_all()
    db.session.begin(subtransactions=True)

    user = User(username='test_user', email='ooo@ooo.ooo.ooo')
    user.password = "right_password"
    db.session.add(user)
    db.session.commit()
    yield user

    db.session.rollback()
    db.session.remove()
    db.drop_all()


@pytest.fixture()
def registered_user(app):
    db.create_all()
    db.session.begin(subtransactions=True)

    user = User(username='new_user',
                email='new_user@mail.com',
                password='password',
                confirmed=False)

    db.session.add(user)
    db.session.commit()
    yield user

    db.session.rollback()
    db.session.remove()
    db.drop_all()


@pytest.fixture()
def confirmed_user(app):
    db.create_all()
    db.session.begin(subtransactions=True)

    user = User(username='new_user',
                email='new_user@mail.com',
                password='password',
                confirmed=True)

    db.session.add(user)
    db.session.commit()
    yield user

    db.session.rollback()
    db.session.remove()
    db.drop_all()


@pytest.fixture
def client(app):
    app.config['TESTING'] = True
    client = app.test_client()
    return client


def generate_credentials(username, password):
    import base64
    credentials = (username + ':' + password).encode('utf-8')
    return base64.b64encode(credentials).decode('utf-8').strip('\r\n')


# GIVEN: client
def test_get_token_when_no_username_no_password_then_401(client):
    # WHEN
    response = client.get('/auth/token')
    # THEN
    assert response.status_code == 401


# GIVEN: client
@pytest.mark.usefixtures('prepare_one_user')
def test_get_token_when_error_password_then_401(client):
    # WHEN
    credentials = generate_credentials("test_user", "error_password")
    response = client.get('/auth/token', headers={'Authorization': 'Basic ' + credentials})
    # THEN
    assert response.status_code == 401


# GIVEN: client
@pytest.mark.usefixtures('prepare_one_user')
def test_get_token_when_right_password_then_200(client):
    # WHEN
    credentials = generate_credentials("test_user", "right_password")
    response = client.get('/auth/token', headers={'Authorization': 'Basic ' + credentials})
    # THEN
    assert response.status_code == 200


# GIVEN: client
@pytest.mark.usefixtures('prepare_one_user')
def test_get_user_when_no_token_then_401(client):
    # WHEN
    response = client.get('/user/test_user')
    # THEN
    assert response.status_code == 401


# GIVEN: client
@pytest.mark.usefixtures('prepare_one_user')
def test_get_user_when_error_token_then_401(client):
    # WHEN
    response = client.get('/user/test_user', headers={'Authorization': 'Bearer ' + 'error_token'})
    # THEN
    assert response.status_code == 401


# GIVEN: client
def test_get_user_when_right_token_then_200(client, prepare_one_user):
    # WHEN
    token = prepare_one_user.generate_auth_token()
    response = client.get('/user/test_user', headers={'Authorization': 'Bearer ' + token})
    # THEN
    assert b'test_user' in response.get_data()
    assert response.status_code == 200


# GIVEN: client
@pytest.mark.usefixtures('prepare_one_user')
def test_register_when_no_registered_user_then_send_confirmation_email(client):

    # WHEN
    new_user = {'username': 'new_user',
                'email': 'new_user@mail.com',
                'password': 'password'}

    with mock.patch('app.auth.send_email') as mock_send_email:
        response = client.post('/auth/register', data=new_user)
        assert response.status_code == 200
        assert mock_send_email.called


# GIVEN: client
@pytest.mark.usefixtures('prepare_one_user')
def test_register_when_registered_user_then_400(client):
    # WHEN
    email_already_registered_user = {'username': 'new_user',
                                     'email': 'ooo@ooo.ooo.ooo',
                                     'password': 'password'}

    with mock.patch('app.auth.send_email') as mock_send_email:
        response = client.post('/auth/register', data=email_already_registered_user)
        assert b'Email already registered.' in response.get_data()
        assert response.status_code == 400
        mock_send_email.assert_not_called()

    # WHEN
    username_already_registered_user = {'username': 'test_user',
                                        'email': 'new@mail.com',
                                        'password': 'password'}

    with mock.patch('app.auth.send_email') as mock_send_email:
        response = client.post('/auth/register', data=username_already_registered_user)
        assert b'Username already in use.' in response.get_data()
        assert response.status_code == 400
        mock_send_email.assert_not_called()


def test_confirm_email_when_registered_user_then_200(client, registered_user):

    confirm_token = registered_user.generate_confirmation_token()

    response = client.get('/auth/confirm/' + confirm_token)

    assert b'You have confirmed your account' in response.get_data()
    assert response.status_code == 200


def test_confirm_email_when_confirmed_user_then_400(client, confirmed_user):

    confirm_token = confirmed_user.generate_confirmation_token()

    response = client.get('/auth/confirm/' + confirm_token)

    assert b'Email already confirmed' in response.get_data()
    assert response.status_code == 400
