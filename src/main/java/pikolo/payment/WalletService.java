package pikolo.payment;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final PaymentMapper walletMapper;

    @Transactional
    public void topUp(Long userSeq, Long amount) {
        WalletDTO wallet = walletMapper.findWallet(userSeq);

        if (wallet == null) {
            // 처음 충전 → insert
            walletMapper.insertWallet(userSeq, amount);
        } else {
            // 기존 지갑 → update
            walletMapper.updateWalletBalance(userSeq, amount);
        }
    }

    public Long getCurrentBalance(Long userSeq) {
        WalletDTO wallet = walletMapper.findWallet(userSeq);
        return wallet == null ? 0L : wallet.getCurrentBalance();
    }
}
