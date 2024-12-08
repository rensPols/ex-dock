package com.ex_dock.ex_dock.backend.v1.router.auth

class AuthProvider() {

  val publickKey =
    "-----BEGIN PUBLIC KEY-----\n" +
      "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgLy6sZ9SzmPEuBIuccLV\n" +
      "FA3oSoZsGPGSYS7mVHd7olpUp5WODLFpeLFUMruL+de+NwgZnrDn+MaFiM86WLYn\n" +
      "o6qFSS1ZYJ7dVJPtCKkQ/N4XKxPFZGpsZMyCdDKTRD8q7eEJCXLE5iGp61/6LKyf\n" +
      "Ghlge/W+w4Z3k8OjfJYOLHabpB28GatkfmkxRXDzKCF3QnZXaNFOytZ2lJfC2Jc8\n" +
      "oQUCoaxzGlVHiexfvutH6zmxCaJTmpbBJ3lNKqC25cj61NcojyyVUPSVeFKTN6z5\n" +
      "EYEey1ssMuUJRb5VqjW9j/egT6vrgvllZ8fbVH6nAOF3uttbSDmcQJKN+9GqqHhJ\n" +
      "jQIDAQAB\n" +
      "-----END PUBLIC KEY-----"

  val privateKey =
    "-----BEGIN PRIVATE KEY-----\n" +
      "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCAvLqxn1LOY8S4\n" +
      "Ei5xwtUUDehKhmwY8ZJhLuZUd3uiWlSnlY4MsWl4sVQyu4v51743CBmesOf4xoWI\n" +
      "zzpYtiejqoVJLVlgnt1Uk+0IqRD83hcrE8VkamxkzIJ0MpNEPyrt4QkJcsTmIanr\n" +
      "X/osrJ8aGWB79b7DhneTw6N8lg4sdpukHbwZq2R+aTFFcPMoIXdCdldo0U7K1naU\n" +
      "l8LYlzyhBQKhrHMaVUeJ7F++60frObEJolOalsEneU0qoLblyPrU1yiPLJVQ9JV4\n" +
      "UpM3rPkRgR7LWywy5QlFvlWqNb2P96BPq+uC+WVnx9tUfqcA4Xe621tIOZxAko37\n" +
      "0aqoeEmNAgMBAAECggEAAiX5dkYIC2UDfxA2nDQPJZLcgdnfwJl6GDXAfD+ziCW6\n" +
      "bJgvUwDm7K+w4wPZiyV8NCOQzI174Yfr6eOOFWA80HC5DAaTYOxlxH06IxSHMbZs\n" +
      "a+NG6QJppVOlswC2ZRjUw5F80B6NMKChMEoIEmodtuweKGi/x+Lc6KdcJBPGUBxI\n" +
      "ii0ii7xVQ9UGjWnJCHnqxrnpzr7cHKTemC9ac0fI2Uj+jG2grdQBbduyggidmhkr\n" +
      "+CLfGB2A6t7WSTGPyUHSl4qy1LwUJCjxggsKHadKTDxedHZwqzSb2DxijM5eUwO6\n" +
      "eos4xByn7sIofS+yLGSwexvrVVNjKMptxBfxr0ptNQKBgQC1Wg4H7LxkSC/4G24j\n" +
      "s61PIjOIyRQ3hL1uzGmQwj6yU8upL2iNqjFIok1rhqjUQuiU43K0we4R1aBFk+4Y\n" +
      "ja5Pc26roNSN3igakmJXtk6NRmm00TeNwM1YyACxvL6RuZ8yJnHwY4r5shvPbHXz\n" +
      "9jpFdpmcgekDNkdWy+dVKxaHowKBgQC1umpbGeyA/g44M/9F1esp7RI6iMySv1pc\n" +
      "CUP40xyCP680wPOBYZ+u4g0yFBrmf8v7xlSg4MomYFcA3xAE6VJ+HmULsYFpUF6y\n" +
      "2GPUCb1PlMk6L+xEuMvWXqIR0fEYAd78ajahoMaoFP9RvHPmSHskRPGCj19jFVBQ\n" +
      "HGYiLCG9DwKBgQCFYJ5BJdPIzW66QzJV/6fPM5BDYeAElRPdkWlyleoWrZpz6/Ix\n" +
      "fqKQkQ3vrzIsKql0F3QdjSPS6hLeGVZbqJgyxur2P2sUi/di05aQe/x52veTjOwW\n" +
      "zV45lZ8tGWvvMV3sPGpAKnXj/yKFA3gc3VMuE3QWr1T4j8sYAw84jGAdkQKBgGt4\n" +
      "O892bEP4eqZIMc217WWU+rO9FOYv3ZsSK61qA7EPQmj7NsYr2ohMzKrx8tqfdx2F\n" +
      "M6UUatf5H1q3j7yn0w4coXsh1TtXuTkg+SB7RgZbIgmUL7CQbJNw0X2iX2boLFuv\n" +
      "4HEDKJhcGoXW4d+su44+a2jfqvRotV86/Dd3S9iHAoGAOBIIsp4Pe0ix7kKhuX8l\n" +
      "KLtOXw1f6D9w7MLuBzkGm0mkCbqKd5GMMjrn6+HR8G0HtZDwrIfZFFxe3f+Fh/55\n" +
      "+QWeSM/3dWXx7+aODCA9Y4cFvdngo9VxtKQa1h9LQHks1AFpJK26E+Pj09wxp9BQ\n" +
      "Mmfwel+wdvOlcLmLzW9V6Ig=\n" +
      "-----END PRIVATE KEY-----\n"
}
