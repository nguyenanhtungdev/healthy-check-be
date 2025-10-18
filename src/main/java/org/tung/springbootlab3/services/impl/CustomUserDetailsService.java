package org.tung.springbootlab3.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.tung.springbootlab3.model.Account;
import org.tung.springbootlab3.model.User;
import org.tung.springbootlab3.repository.AccountRepository;
import org.tung.springbootlab3.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;
    //Spring Security cần một UserDetailsService để biết cách lấy user từ DB
    //Nó tìm xem có Bean nào implement interface UserDetailsService trong context không.
    //Nếu có: Load thông tin người dùng khi đăng nhập (qua Basic Auth hoặc form login),So sánh password đã mã hóa với input, Cấp quyền (roles) cho user
    //Nếu không có, Spring mới tạo user mặc định (user + password ngẫu nhiên)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        //Tạo ra một đối tượng UserDetails mà Spring Security hiểu được, từ dữ liệu User bạn lấy trong database
        //Đoạn code đó là bước trung gian chuyển đổi dữ liệu từ database sang đối tượng UserDetails để Spring Security có thể xác thực và phân quyền cho người dùng.
        return org.springframework.security.core.userdetails.User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("USER")
                .build();
    }
}