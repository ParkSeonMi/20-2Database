package bank;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Account {
	Connection con = null;  // 접속 객체
	Scanner sc = new Scanner(System.in);
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Date now = new Date();  // 생성날짜 출력을 위한 코드
	String present = sdf.format(now);
	
	public Account() {  // java.sql에 연결하는 코드
		String Driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/bank?&serverTimezone=Asia/Seoul";
		String userid = "root";				// mysql 아이디
		String userpwd = "yook!60502";		// mysql 비밀번호
		
		try {  /* 드라이버를 찾는 과정 */
			System.out.println("드라이버 연결 시도...");
			Class.forName(Driver);
			System.out.println("드라이버 로드 성공\n");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {  /* 데이터베이스를 연결하는 과정 */
			System.out.println("데이터베이스 연결 시도...");
			con = DriverManager.getConnection(url, userid, userpwd);
			System.out.println("데이터베이스 연결 성공!\n");
		} catch (SQLException e) {
			System.out.println("데이터베이스 연결 실패");
			e.printStackTrace();
		}
	}

	public void checkAcc() {  /* 예금계좌 조회 */
		//주민번호 입력 -> 예금계좌, 고객정보 출력 
		try {
			System.out.print("고객 주민번호 입력>>> ");
			String custId = sc.nextLine();
			
			String sql;  // 문자열 sql에 수행할 SQL문을 입력
			// csname=고객명, accid=예금계좌ID, acckind=예금계좌종류, accbalance=예금잔고, cardyn=카드신청여부, openday=예금개설일
			sql = "SELECT csname, accid, acckind, accbalance, cardyn, openday "
					+ "FROM account a, customer c "
					+ "WHERE c.custid='" + custId + "' and accname=csname";
			
			// SQL문을 실행하는 Statement 객체 생성
			Statement st = con.createStatement();
			// SQL문을 실행하여 ResultSet 객체 생성
			ResultSet rs = null;
			// executeQuery return value는 ResultSet
			rs = st.executeQuery(sql);
			
			System.out.println("고객명\t예금계좌ID\t\t예금계좌종류\t예금잔고\t카드신청여부\t예금개설일");
			// ResultSet의 첫 번째 필드는 1부터 시작
			while (rs.next()) {
				System.out.print(rs.getString(1));			// rs.getString("csname")
				System.out.print("\t" + rs.getInt(2));		// rs.getInt("accid")
				System.out.print("\t\t" + rs.getString(3));	// rs.getString("acckind")
				System.out.print("\t\t" + rs.getInt(4));	// rs.getInt("accbalance")
				System.out.print("\t" + rs.getString(5));	// rs.getString("cardyn")
				System.out.println("\t\t" + rs.getDate(6));	// rs.getDate("openday")
			}
			System.out.println();
			
			rs.close();		// 외부 자원이므로 반드시 반납
			st.close();
			con.close();	// 접속 객체 종료
		} 
		catch (SQLException e) {
			System.err.println(e.getMessage());
			System.out.println("오류 발생");
		}
	}

	public void checkHis() {  /* 예금계좌 거래내역 조회 */
		// 주민번호 입력 -> 예금계좌id 출력 -> 선택 -> 거래내역 시간역순 출력
		System.out.print("고객 주민번호 입력>>> ");
		String custId = sc.nextLine();
		try {
			Statement st = con.createStatement();
			// accid=예금계좌ID, accname=예금자이름
			String sql = "SELECT accid, accname from account a, customer c "
					+ "where a.custid='" + custId + "' and accname=csname";
			
			ResultSet rs = null;
			rs = st.executeQuery(sql);
			
			System.out.println("이름\t계좌번호");
			while(rs.next()) {
				System.out.print(rs.getString(2));		// rs.getString("accid")
				System.out.println("\t"+rs.getInt(1));	// rs.getInt("accname")
			}  // 계좌번호 출력
			
			System.out.print("계좌번호 입력>>> ");
			int accNum = sc.nextInt();
			// acchistory=예금계좌 거래내역, accid=예금계좌ID, dwday=입출금날짜
			sql = "select * from acchistory where accid='" + accNum + "' "
					+"order by dwday desc";  // 시간역순
			
			rs = st.executeQuery(sql);
			
			System.out.println("<거래내역>");
			System.out.println("계좌번호\t입출금날짜\t\t거래번호\t예금구분\t예금내용\t거래금액\t예금잔고");
			while(rs.next()) {
				System.out.print(rs.getString(1));			// rs.getString("accid")
				System.out.print("\t"+rs.getString(2));		// rs.getString("dwday")
				System.out.print("\t"+rs.getString(3));		// rs.getString("hisnum")
				System.out.print("\t"+rs.getString(4));		// rs.getString("classify")
				System.out.print("\t"+rs.getString(5));		// rs.getString("content")
				System.out.print("\t"+rs.getString(6));		// rs.getString("amount")
				System.out.println("\t"+rs.getString(7));	// rs.getString("hisbalance")
			}
			System.out.println();
			
			rs.close();		// 외부 자원이므로 반드시 반납
			st.close();
			con.close();	// 접속 객체 종료
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.out.println("오류발생");
		}
	}

	public void createCard() {  /* 카드 생성 */
		// 주민번호,예금계좌ID 입력 -> 카드 생성 -> 카드리스트 예금계좌로 그룹핑 -> 예금계좌ID,예금계좌종류+카드ID~카드 종류 출력
		System.out.print("고객 주민번호 입력>>> ");
		String custId = sc.nextLine();
		System.out.print("예금계좌ID 입력>>> ");
		int accid = sc.nextInt();
		
		try {
			ResultSet rs = null;
			String sql;
			
			sql = "INSERT into card values(?,?,?,NULL,?,?,?)";
			
			PreparedStatement pst = con.prepareStatement(sql);
						
		
			pst.setInt(1, (int)(Math.random()*1000)+2000);  // 1000자리 카드번호
			pst.setString(2, present);
			pst.setInt(3, 5000000);
			pst.setString(4, "체크카드");
			pst.setString(5, custId);
			pst.setInt(6, accid);
			pst.executeUpdate();
			
			// accid=예금계좌ID,acckind=예금계좌종류,cardid=카드ID,isuueday=카드신청일자,limitpay=카드한도금액,cardkind=카드종류
			sql = "select concat(a.accid,', ',acckind), concat(cardid,', ',isuueday,\", \",limitpay,\", \",cardkind)\r\n " + 
					"from account a, card c \r\n " + 
					"where a.accid=c.accid and c.custid=a.custid ";
			
			rs = pst.executeQuery(sql);
			
			System.out.println("계좌 정보(계좌번호,계좌종류)\t\t카드 정보(카드번호,생성날짜,한도금액,카드종류)");
			while(rs.next()) {
				System.out.print(rs.getString(1));					// rs.getString(계좌 정보)
				System.out.println("\t\t\t"+rs.getString(2));		// rs.getString(카드 정보)
			}
			System.out.println();
			
			rs.close();
			pst.close();
			con.close();
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.out.println("오류발생");
		}
	}

	public void deleteAcc() {  /* 예금계좌 삭제 */
        // 예금계좌ID 입력 -> 예금계좌 삭제(관련 카드,거래내역 함께) -> 잔여 카드리스트,거래내역 나누어 출력
        System.out.print("예금계좌ID 입력>>> ");
        int accId = sc.nextInt();

        try {
        	con.setAutoCommit(false);
        	PreparedStatement pst = null;
        	ResultSet rs = null;
        	
        	// account=예금계좌,accid=예금계좌ID
        	String sql = "delete from account where accid='"+accId+"'";
        	
        	pst = con.prepareStatement(sql);
        	pst.executeUpdate(sql);
        	con.commit();
        	
        	sql = "select * from card";
        	
        	pst = con.prepareStatement(sql);
        	rs = pst.executeQuery(sql);
        	
        	System.out.println("<카드리스트>");
        	System.out.println("카드번호\t카드신청일자\t카드한도금액\t카드결제일자\t카드종류\t고객주민번호\t계좌번호");
        	while(rs.next()) {
        		System.out.print(rs.getInt(1));				// rs.getInt("cardid")
        		System.out.print("\t"+rs.getString(2));		// rs.getString("isuueday")
        		System.out.print("\t"+rs.getInt(3));		// rs.getInt("limitpay")
        		System.out.print("\t\t"+rs.getString(4));		// rs.getString("paymentday")
        		System.out.print("\t\t"+rs.getString(5));		// rs.getString("cardkind")
        		System.out.print("\t"+rs.getString(6));		// rs.getString("custid")
        		System.out.println("\t\t"+rs.getInt(7));		// rs.getInt("accid")
        		}
        	
        	// acchistory=예금계좌 거래내역
        	sql = "select * from acchistory";
        	
        	pst = con.prepareStatement(sql);
        	rs = pst.executeQuery(sql);
        	
        	System.out.println("<거래내역>");
        	System.out.println("계좌번호\t입출금날짜\t\t거래번호\t\t예금구분\t\t예금내용\t거래금액\t\t예금잔고");
        	while(rs.next()) {
        		System.out.print(rs.getString(1));			// rs.getString("accid")
        		System.out.print("\t"+rs.getString(2));		// rs.getString("dwday")
        		System.out.print("\t"+rs.getString(3));		// rs.getString("hisnum")
        		System.out.print("\t\t"+rs.getString(4));		// rs.getString("classify")
        		System.out.print("\t\t"+rs.getString(5));		// rs.getString("content")
        		System.out.print("\t"+rs.getString(6));		// rs.getString("amount")
        		System.out.println("\t\t"+rs.getString(7));	// rs.getString("hisbalance")
        		}
        	con.commit();
        	
        	System.out.println();
        	
        	rs.close();
        	pst.close();
        	con.close();
        	
        } catch (SQLException e) {
        	System.err.println(e.getMessage());
        	System.out.println("오류발생");
        	}
        }

	public void depositAcc() {  /* 입금 */
        // 예금계좌ID,금액 입력 -> 거래내역 추가 -> 예금잔고 변경 -> 잔고,거래내역 최근기록 출력
		System.out.print("예금계좌ID 입력>>> ");
        int accId = sc.nextInt();
        System.out.print("예금할 금액 입력>>> ");
        int amt = sc.nextInt();  // 입금액
        
        try {
        	con.setAutoCommit(false);  // 트랜잭션 시작
        	Statement st = con.createStatement();
        	ResultSet rs = null;
        	
        	String sql;
        	// accbalance=예금잔고,hisnum=거래번호,accid=예금계좌ID
        	sql = "SELECT accbalance, max(hisnum) FROM account a, accHistory WHERE a.accid='"+accId+"'";
        	
        	rs = st.executeQuery(sql);
        	int accAmount = 0;
        	int pastHisNum = 0;
        	
        	while(rs.next()) {
        		accAmount = rs.getInt("accbalance");  // 현재 잔고
        		pastHisNum = rs.getInt("max(hisnum)");
        		}
        	
        	accAmount = amt+accAmount;  // 현재 잔고+입금액
        	
        	sql = "UPDATE account SET accbalance=? where accid=?";  // account 테이블 수정문
        	String sql2 = "INSERT into acchistory() values(?,?,?,?,\"예금\",?,?)";  // acchistory 테이블 추가문
        	
        	PreparedStatement pst = con.prepareStatement(sql);
        	pst.setInt(1, accAmount);
        	pst.setInt(2, accId);
        	pst.executeUpdate();
        	pst = con.prepareStatement(sql2);
        	con.commit();
        	
        	pst.setInt(1,accId);
        	pst.setString(2, present);
        	pst.setInt(3, pastHisNum+1);
        	pst.setString(4, "인터넷뱅킹");
        	pst.setInt(5, amt);
        	pst.setInt(6, accAmount);
        	pst.executeUpdate();
        	con.commit();
        	
        	String sql3="select * from acchistory where hisnum='"+(pastHisNum+1)+"'";  // 거래내역 출력을 위한 코드
        	pst=con.prepareStatement(sql3);
        	rs=pst.executeQuery();
        	
        	System.out.println("계좌번호\t입출금날짜\t\t거래번호\t예금구분\t예금내용\t거래금액\t예금잔고");
        	while (rs.next()) {
        		System.out.print(rs.getInt(1));				// rs.getInt("accid")
        		System.out.print("\t"+rs.getString(2));		// rs.getString("dwday")
        		System.out.print("\t"+rs.getInt(3));		// rs.getInt("hisnum")
        		System.out.print("\t"+rs.getString(4));		// rs.getString("classify")
        		System.out.print("\t"+rs.getString(5));		// rs.getString("content")
        		System.out.print("\t"+rs.getInt(6));		// rs.getInt("amount")
        		System.out.println("\t"+rs.getInt(7));		// rs.getInt("hisbalance")
        		}
        	con.commit();
        	
        	System.out.println();
        	
        	rs.close();
        	st.close();
        	con.close();
        	
        } catch (Exception e) {
        	System.err.println(e.getMessage());
        	System.out.println("오류발생");
        	}
        }
	}