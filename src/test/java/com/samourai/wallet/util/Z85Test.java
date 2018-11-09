package com.samourai.wallet.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Z85Test {
    private static final Z85 z85 = Z85.getInstance();

    @Test
    public void testZ85() throws Exception {
        byte[] data = "A purely peer-to-peer version of electronic cash would allow online payments to be sent directly from one party to another without going through a financial institution. Digital signatures provide part of the solution, but the main benefits are lost if a trusted third party is still required to prevent double-spending. We propose a solution to the double-spending problem using a peer-to-peer network. The network timestamps transactions by hashing them into an ongoing chain of hash-based proof-of-work, forming a record that cannot be changed without redoing the proof-of-work. The longest chain not only serves as proof of the sequence of events witnessed, but proof that it came from the largest pool of CPU power. As long as a majority of CPU power is controlled by nodes that are not cooperating to attack the network, they'll generate the longest chain and outpace attackers. The network itself requires minimal structure. Messages are broadcast on a best effort basis, and nodes can leave and rejoin the network at will, accepting the longest proof-of-work chain as proof of what happened while they were gone.1234567890-_)({(@".getBytes();

        String encoded = z85.encode(data);
        Assertions.assertEquals("k{gb?A+f6jaAg[6AXhxKePtojAV/KpA=U@kzxJ>faz2w2v@#:fzFrP6v{%B(aA}:Dy?aU%y&13laA8dlx(mG8Aaa3ewO#QhaARJAvR3W6wO#Pjwnc6}v@#KjazbUjz6i+mwGU@2A==NHBz&pgzF%SgwPw]qx([3bB-X:sz!T94aARpqz/]YaayMy8x(mu%v}/v/azC.mBzk&tBzkVhe*9L@xk8D@y-)!kxkRd1B-I5maAhvtC4>F#aAg+fBrCHlaARpdaAIEqB-.tozy=]tB-X:FxK@r6vqfQ4vR6B*w[=vdayPq4az+$qBrCpfayMymA=(CBwN({9xLzG$aAg+fBA]0yB0bKCx(4PdA+flkx(W=}aARJAAb]JkwO#Pjwn=Q1y?khtAaJ]$x(mMoaxJa%Ab](oz/YFjvixSfy&%&lz/cXtzY<dnwGUJ4BZ/e#ePU(xzE^E0xcqelz^)J]z6i$xx(mMavixJ2wPxwAz.k{twPw]hwPScmA+^oIra]?=zE){lz/PRoBzkP6B97&cAc0dzA:-<bvpKy[z/fRsvTf8fvrl9@zF78lxK#36x(n3hayPdgz/fiax(mMav}Yp+zxJ>fazth8xF0X4B7F(8Ab](nw?v}keQ8bNyB*Puz/PYezF782aAz46z/PwhBzb98ay!?$zF%RtvR3V(xKL^>wN({cx([3bB-X:DwN]Z[zF78lxK@r9A=k(dePkkMCw?Iqe*abbwGU/czF9K4BrC7bvqfQ4zF%Rtz/fxpaAIamC4C.qvriMgA=k(daA7<mBzbkdB7GulwO#0?aA7<mwP?T5BAnNGx([l7B8$]6efFzABrCKyz/okgBzb98azC{uv{%j=azbUjz6i}lwGU!$A+w%8BrCKvz!{Lmw/%DqruMm0Cv+!Eavg5>y&si7ayPslvixz}yhXcbBA]0Ew/%DqruMm0Cv+!qx(+zbz/fVqz!%l3wftubaz#adwPF#oxKM09vrb{4zF%Rtv@Dp8wPzj3x(mMaBz&pgBAy@3yAN.jwGU(4BA.UzyB*PIxK#Dpy?$kbwO#71vru66Bzbkdy&si7wPI@ov}Yp+zxJw9wft/kBz$U#wGUA6BywV{wPz&Aaxi2=az$+jCw?IqazC}swO+%3A+flkx(W^aaz>?gx(do{aAITBBZ]JoA+c<Co>wx!vp%d)ayPq4ayYwfvpS>RB95KyzxJvgvR6R5az2d%z/P}xvQTs(B1wCxzE:(cz!9B2ay!?$az+Q$C4z!8zE:(gwOMc$zxK4mwGU(4BA.UzyAN5caA}Koy+cYqv}f7?BzkS9aARpdaz+$lxjVdaaAhvtz!pOtw?wjBA+=!cxKLQ)ayPslAb](nw/#27aA}HcBrCm9AbYxawN({cxLzo]aARpdC{4rxA+cwaz/fbsf!$Kwh8WxMiwr3(djxJ>kM", encoded);

        byte[] decoded = data;
        Assertions.assertEquals(data, decoded);
    }

}
