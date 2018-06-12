package YingYingMonster.LetsDo_Phase_III.serviceImpl;

import YingYingMonster.LetsDo_Phase_III.entity.Ability;
import YingYingMonster.LetsDo_Phase_III.entity.Label;
import YingYingMonster.LetsDo_Phase_III.entity.User;
import YingYingMonster.LetsDo_Phase_III.repository.UserRepository;
import YingYingMonster.LetsDo_Phase_III.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserServiceImpl implements UserService {


	@Autowired
	UserRepository userRepository;


    @Override
    @Deprecated
    public boolean userExist(String id) {
        return false;
    }

    @Override
    public User register(User user) {

        return userRepository.saveAndFlush(user);
    }

    @Override
    public User login(long id, String pw) {
        return userRepository.findByIdAndPw(id, pw);
    }

    @Override
    public User modify(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User getUser(long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findUsersByName(String name) {
        return userRepository.findByNameLike(name);
    }

    @Override
    public User financeTransaction(long userId, long money) {
        userRepository.changeMoney(userId, money);
        return userRepository.findById(userId);
    }

    @Override
    public List<User> findByStringAttr(String attr) {
        return userRepository.findByStringAttr(attr);
    }

    @Override
    public List<Label> getUserLabels(long userId) {
        return null;
    }

    @Override
    public List<Ability> getUserAbilities(long userId) {
        return userRepository.findById(userId).getAbilities();
    }
}
