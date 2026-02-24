package Serveur.dao;

import commons.modele.User;

public interface IUserDao {

	User getUserByLogin(String login);
}
