package bank;

import java.sql.*;
import java.util.*;

public class BankMain {
	public static void main(String[] args) {
		boolean loop = true;
		Scanner sc = new Scanner(System.in);
		while (loop) {
			Account account = new Account();
			System.out.println("--------- 은행 업무 메뉴 ----------");
			System.out.println("1. 예금계좌 조회 ");
			System.out.println("2. 예금계좌 거래내역 조회 ");
			System.out.println("3. 카드 생성");
			System.out.println("4. 예금계좌 삭제");
			System.out.println("5. 입금하기");
			System.out.println("9. 종료하기");
			System.out.print("번호 입력>>> ");
			int n = sc.nextInt();

			switch (n) {
			case 1:
				account.checkAcc();
				break;
			case 2:
				account.checkHis();
				break;
			case 3:
				account.createCard();
				break;
			case 4:
				account.deleteAcc();
				break;
			case 5:
				account.depositAcc();
				break;
			case 9:
				System.out.println("프로그램을 종료합니다.");
				sc.close();
				loop = false;
				break;
			}
		}
	}
}

